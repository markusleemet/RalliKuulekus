package cs.ut.ee.rallikuulekus.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema
import cs.ut.ee.rallikuulekus.entities.SignRotation
import cs.ut.ee.rallikuulekus.fragments.FragmentEdit
import cs.ut.ee.rallikuulekus.fragments.FragmentMenu
import cs.ut.ee.rallikuulekus.functions.generateSignList
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import cs.ut.ee.rallikuulekus.viewModels.SignsViewModel
import cs.ut.ee.rallikuulekus.views.Grid
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity(), FragmentMenu.OnFragmentInteractionListener, FragmentEdit.OnFragmentInteractionListener {
    private lateinit var model: SignsViewModel
    private val SIGN_CLASS_SELECTION_CONSTANT = 1234
    private val CHANGE_SIGN_CONSTANT = 1235
    private val neededPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_main)
        model = ViewModelProviders.of(this)[SignsViewModel::class.java]

        //permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, neededPermissions, 123)
        }

        //Add grid as background if constraint layout has size
        constraint_layout_main.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val grid = Grid(this@MainActivity, constraint_layout_main.width, constraint_layout_main.height)
                grid.id = View.generateViewId()
                constraint_layout_main.addView(grid)

                //Further actions depend on open mode
                thread {
                    if (intent!!.hasExtra("name")){
                        model.initNewSchema(intent.getStringExtra("name")!!, intent.getStringExtra("description")!!)
                    }else{
                        val id = intent.getIntExtra("id",-1)
                        model.initExistingSchema(id)
                        val signsForTheSchema = model.getSignsToPutOnScreen(id)
                        signsForTheSchema.forEach {
                            runOnUiThread {
                                putSignOnTheSchema(it)
                            }
                        }
                    }
                    runOnUiThread {
                        initStartAndFinishSigns(false)
                    }
                }

                constraint_layout_main.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        constraint_layout_main.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        closeAllFragments()
                    } else {
                        val xCoordinate = event.rawX - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) / 2
                        val yCoordinate = event.rawY - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight) / 2
                        openSignClassSelectionActivityToAddNewSign(xCoordinate, yCoordinate)
                    }
                }
            }
            true
        }

        fragment_container_menu.setOnTouchListener { _, _ ->
            true
        }

        fragment_container_edit.setOnTouchListener { _, _ ->
            true
        }


    }


    fun initStartAndFinishSigns(newSchema: Boolean){
        Log.i("rallikuulekusLog", "Function initStartAndFinishSigns")

        //Init start sign
        val startImageView = ImageView(this)
        startImageView.tag = "start"
        startImageView.id = View.generateViewId()
        startImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_start, null))
        val imageViewParams = ConstraintLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth), resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight))
        startImageView.layoutParams = imageViewParams

        //Init finish sign
        val finishImageView = ImageView(this)
        finishImageView.tag = "finish"
        finishImageView.id = View.generateViewId()
        finishImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_finis, null))
        finishImageView.layoutParams = imageViewParams



        if (newSchema) {
            Log.i("rallikuulekusLog", "Function initStartAndFinishSigns: new schema -> $newSchema")
            val startXCoordinate: Float = (constraint_layout_main.width - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) + 100) / 2f
            val startYCoordinate = (constraint_layout_main.height - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight) + 100) / 2f
            val finishXCoordinate = (constraint_layout_main.width - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth)) / 2f
            val finishYCoordinate = (constraint_layout_main.height - resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight)) / 2f

            startImageView.x = startXCoordinate
            startImageView.y = startYCoordinate
            finishImageView.x = finishXCoordinate
            finishImageView.y = finishYCoordinate

        }else{
            Log.i("rallikuulekusLog", "Function initStartAndFinishSigns: new schema -> $newSchema")

            startImageView.x = model.startXCoordinate
            startImageView.y = model.startYCoordinate
            finishImageView.x = model.finishXCoordinate
            finishImageView.y = model.finishYCoordinate
        }

        startImageView.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val xDelta = lastX - event!!.rawX
                        val yDelta = lastY - event!!.rawY
                        if (yDelta.absoluteValue > 3 || xDelta > 3) {
                            startImageView.x = startImageView.x - xDelta
                            startImageView.y = startImageView.y - yDelta
                            lastX = event!!.rawX
                            lastY = event!!.rawY
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event!!.rawX
                        lastY = event!!.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        model.startXCoordinate = v!!.x
                        model.startYCoordinate= v.y
                    }
                }
                return true
            }
        })

        finishImageView.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val xDelta = lastX - event!!.rawX
                        val yDelta = lastY - event!!.rawY
                        if (yDelta.absoluteValue > 3 || xDelta > 3) {
                            finishImageView.x = finishImageView.x - xDelta
                            finishImageView.y = finishImageView.y - yDelta
                            lastX = event!!.rawX
                            lastY = event!!.rawY
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event!!.rawX
                        lastY = event!!.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        model.finishXCoordinate = v!!.x
                        model.finishYCoordinate= v.y
                    }
                }
                return true
            }
        })

        // Add created signs to constraint layout
        constraint_layout_main.addView(finishImageView)
        constraint_layout_main.addView(startImageView)
    }

    fun openSignClassSelectionActivityToAddNewSign(xCoordinate: Float, yCoordinate: Float){
        val signClassSelectionIntent = Intent(this, SignClassSelectionActivity::class.java)
        signClassSelectionIntent.putExtra("x", xCoordinate)
        signClassSelectionIntent.putExtra("y", yCoordinate)
        startActivityForResult(signClassSelectionIntent, SIGN_CLASS_SELECTION_CONSTANT)
    }

    fun openSignClassSelectionActivityToChangeExistingSign(signIndex: Int){
        val signClassSelectionIntent = Intent(this, SignClassSelectionActivity::class.java)
        signClassSelectionIntent.putExtra("index", signIndex)
        startActivityForResult(signClassSelectionIntent, CHANGE_SIGN_CONSTANT)
    }

    fun openMenuFragment(v: View){
        closeAllFragments()
        val menuFragment = FragmentMenu()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_menu, menuFragment)
        fragmentTransaction.addToBackStack("menu")
        fragmentTransaction.commit()
    }

    private fun openEditFragment(index: Int){
        closeAllFragments()
        val fragmentEdit = FragmentEdit.newInstance(index)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container_edit, fragmentEdit)
        transaction.addToBackStack("edit")
        transaction.commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }

    override fun saveAndExportBitmap() {
        saveSchema()
        closeAllFragments()
        button_menu.isVisible = false
        val bitmapToSave = createBitmap()
        button_menu.isVisible = true

        val result = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapToSave, model.name , model.description)
        Log.i("eksport", "result: $result")
        /**
        try{
            val file = File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            Log.i("eksport", "path: ${file.path}")

            if (file.mkdirs()) {
                val outFile = File(file, "${model.name}.JPEG")
                Log.i("eksport", outFile.toString())
                FileOutputStream(outFile, false).use {
                    bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
            }else{
                Log.i("eksport", "mkdirs returned false")
            }
        } catch (e: FileNotFoundException) {
            //TOAST to show user information
            val toast = Toast.makeText(
                this,
                "Midagi läks valesti, pilti ei eksporditud!",
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return
        } catch (e: IOException) {
            //TOAST to show user information
            val toast = Toast.makeText(
                this,
                "Midagi läks valesti, pilti ei eksporditud!",
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return
        }

        //TOAST to show user information
        val toast = Toast.makeText(this, "Joonis edukalt eksporditud!", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()**/
    }

    override fun saveSchema() {
        closeAllFragments()
        thread {
            model.saveNewSchemaToDatabase()
        }
        //TOAST to show user information
        val toast = Toast.makeText(this, "Joonis edukalt salvestatud!", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    override fun changeSign(signIndex: Int) {
        supportFragmentManager.popBackStack()
        openSignClassSelectionActivityToChangeExistingSign(signIndex)
    }

    override fun deleteSign(signIndex: Int) {
        supportFragmentManager.popBackStackImmediate()
        constraint_layout_main.removeView(constraint_layout_main.findViewWithTag<ImageView>(signIndex))
        constraint_layout_main.removeView(constraint_layout_main.findViewWithTag<TextView>("textView $signIndex"))
        model.signs.removeAt(signIndex)
        refreshIndexes(signIndex)
    }

    override fun rotateDegrees(degree: Int, signIndex: Int) {
        val imageView = constraint_layout_main.findViewWithTag<ImageView>(signIndex)
        val textView = constraint_layout_main.findViewWithTag<TextView>("textView $signIndex")
        val signOnTheSchema = model.signs[signIndex]
        val signRotation = signOnTheSchema.rotation

        if (signRotation == SignRotation.LEFT || signRotation == SignRotation.RIGHT) {
            textView.x = textView.x - resources.getDimensionPixelSize(R.dimen.rotationElevation)
            textView.y = textView.y - resources.getDimensionPixelSize(R.dimen.rotationElevation)
        }else{
            textView.x = textView.x + resources.getDimensionPixelSize(R.dimen.rotationElevation)
            textView.y = textView.y + resources.getDimensionPixelSize(R.dimen.rotationElevation)
        }
        imageView.rotateToHeading(signOnTheSchema.rotation)
    }

    fun ImageView.rotateToHeading(signRotation: SignRotation){
        val imageView = this
        Log.i("rallikuulekusLog", "ImageView to rotate id -> ${imageView.tag}")

        when (signRotation) {
            SignRotation.TOP -> {
                this.rotation = 0f
            }
            SignRotation.BOTTOM -> {
                this.rotation = 180f
            }
            SignRotation.LEFT -> {
                this.rotation = 270f
            }
            SignRotation.RIGHT -> {
                this.rotation = 90f
            }
        }
    }

    private fun refreshIndexes(removedSignIndex: Int){
        for (view in constraint_layout_main.children) {
            if (view is ImageView && view.tag is Int) {
                val oldIndex = view.tag as Int
                if (oldIndex > removedSignIndex) {
                    val newIndex = oldIndex - 1
                    view.tag = newIndex
                }
            }
            if (view is TextView && view.tag != null) {
                Log.i("rotation", "textView: $view")
                val oldIndex = (view.tag as String).split(" ")[1].toInt()
                if (oldIndex > removedSignIndex) {
                    val newIndex = oldIndex - 1
                    view.tag = "textView $newIndex"
                    view.text = (newIndex + 1).toString()
                }
            }
        }
    }

    private fun createBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(constraint_layout_main.width,
            constraint_layout_main.height,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = constraint_layout_main.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        constraint_layout_main.draw(canvas)
        return bitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_CLASS_SELECTION_CONSTANT && resultCode == Activity.RESULT_OK) {
            val xCoordinate = data!!.getFloatExtra("x", -1f)
            val yCoordinate = data.getFloatExtra("y", -1f)
            val rkClass = data.getIntExtra("class", -1)
            val position = data.getIntExtra("position", -1)
            val sign = generateSignList(rkClass, this)[position]
            val signOnTheSchema = SignOnTheSchema(xCoordinate, yCoordinate, rkClass, sign.drawable, sign.heading, sign.description, position, SignRotation.TOP)
            model.addSignToSigns(signOnTheSchema)
            putSignOnTheSchema(signOnTheSchema)
        }
        if (requestCode == CHANGE_SIGN_CONSTANT && resultCode == Activity.RESULT_OK) {
            val signIndex = data!!.getIntExtra("index", -1)
            val rkClass = data.getIntExtra("class", -1)
            val position = data.getIntExtra("position", -1)
            val sign = generateSignList(rkClass, this)[position]
            constraint_layout_main.findViewWithTag<ImageView>(signIndex).setImageDrawable(sign.drawable)
            model.signs[signIndex].drawable = sign.drawable
            model.signs[signIndex].heading = sign.heading
            model.signs[signIndex].description = sign.description
        }
    }

    private fun putSignOnTheSchema(signOnTheSchema: SignOnTheSchema){
        val signIndex = model.signs.indexOf(signOnTheSchema)
        Log.i("rallikuulekusLog", "Function putSignOnTheSchema: id -> $signIndex")

        val imageView = ImageView(this)
        imageView.tag = signIndex
        imageView.id = View.generateViewId()
        imageView.x = signOnTheSchema.xCoordinate
        imageView.y = signOnTheSchema.yCoordinate
        imageView.setImageDrawable(signOnTheSchema.drawable)
        imageView.rotateToHeading(signOnTheSchema.rotation)
        val imageViewParams = ConstraintLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth), resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight))
        imageView.layoutParams = imageViewParams


        val textView = TextView(this)
        textView.tag = "textView $signIndex"
        textView.id = View.generateViewId()

        if (signOnTheSchema.rotation == SignRotation.LEFT || signOnTheSchema.rotation == SignRotation.RIGHT) {
            textView.x = imageView.x + resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) + 10 - resources.getDimensionPixelSize(R.dimen.rotationElevation)
            textView.y = imageView.y - resources.getDimensionPixelSize(R.dimen.rotationElevation)
        }else{
            textView.x = imageView.x + resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) + 10
            textView.y = imageView.y
        }

        textView.text = (signIndex + 1).toString()
        textView.textSize = resources.getDimension(R.dimen.signNumberTextSize)
        textView.setTextColor(Color.BLACK)

        imageView.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            var drag = false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val xDelta = lastX - event!!.rawX
                        val yDelta = lastY - event!!.rawY
                        if (yDelta.absoluteValue > 3 || xDelta > 3) {
                            imageView.x = imageView.x - xDelta
                            imageView.y = imageView.y - yDelta
                            textView.x = textView.x - xDelta
                            textView.y = textView.y - yDelta
                            lastX = event!!.rawX
                            lastY = event!!.rawY
                            drag = true
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event!!.rawX
                        lastY = event!!.rawY
                        drag = false
                    }
                    MotionEvent.ACTION_UP -> {
                        if (drag == false) {
                            openEditFragment(v!!.tag as Int)
                        }else{
                            model.signs[v!!.tag as Int].xCoordinate = v.x
                            model.signs[v!!.tag as Int].yCoordinate = v.y
                        }
                    }
                }
                return true
            }
        })


        constraint_layout_main.addView(textView)
        constraint_layout_main.addView(imageView)

    }

    private fun closeAllFragments() {
        while (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }
}
