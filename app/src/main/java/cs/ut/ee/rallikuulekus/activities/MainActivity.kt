package cs.ut.ee.rallikuulekus.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema
import cs.ut.ee.rallikuulekus.entities.SignRotation
import cs.ut.ee.rallikuulekus.fragments.FragmentEdit
import cs.ut.ee.rallikuulekus.fragments.FragmentMenu
import cs.ut.ee.rallikuulekus.functions.CapturePhotoUtils
import cs.ut.ee.rallikuulekus.functions.generateSignList
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import cs.ut.ee.rallikuulekus.viewModels.SignsViewModel
import cs.ut.ee.rallikuulekus.views.Grid
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity(), FragmentMenu.OnFragmentInteractionListener, FragmentEdit.OnFragmentInteractionListener {
    private lateinit var model: SignsViewModel
    private val SIGN_CLASS_SELECTION_CONSTANT = 1234
    private val CHANGE_SIGN_CONSTANT = 1235

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_main)
        model = ViewModelProviders.of(this)[SignsViewModel::class.java]

        //Add grid as background if constraint layout has size
        constraint_layout_main.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val grid = Grid(this@MainActivity, constraint_layout_main.width, constraint_layout_main.height)
                grid.id = View.generateViewId()
                constraint_layout_main.addView(grid)
                constraint_layout_main.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        constraint_layout_main.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        closeAllFragments()
                    } else {
                        openSignClassSelectionActivityToAddNewSign(event.x, event.y)
                    }
                }
            }
            true
        }

        fragment_container_menu.setOnTouchListener { v, event ->
            true
        }

        fragment_container_edit.setOnTouchListener { v, event ->
            true
        }
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
        val nameValue = intent.getStringExtra("name")!!
        val descriptionValue = intent.getStringExtra("description")!!
        val menuFragment = FragmentMenu.newInstance(nameValue, descriptionValue)
        val supportFragmentManager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_menu, menuFragment)
        fragmentTransaction.addToBackStack("menu")
        fragmentTransaction.commit()
    }

    private fun openEditFragment(index: Int){
        closeAllFragments()
        val fragmentEdit = FragmentEdit.newInstance(index)
        val supportFragmentManager = supportFragmentManager
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container_edit, fragmentEdit)
        transaction.addToBackStack("edit")
        transaction.commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }

    override fun saveAndExportBitmap(uri: Uri) {
        supportFragmentManager.popBackStackImmediate()
        button_menu.isVisible = false
        val bitmapToSave = createBitmap()
        button_menu.isVisible = true
        val contentResolver = this.contentResolver
        CapturePhotoUtils.insertImage(contentResolver, bitmapToSave,
            intent.getStringExtra("name"),
            intent.getStringExtra("description"))
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
            if (view is ImageView) {
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
            val signOnTheSchema = SignOnTheSchema(xCoordinate, yCoordinate, rkClass, sign.drawable, sign.heading, sign.description)
            model.addSignToSigns(signOnTheSchema)
            createViewGroupForSign(signOnTheSchema)
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

    private fun createViewGroupForSign(signOnTheSchema: SignOnTheSchema){
        val signIndex = model.signs.indexOf(signOnTheSchema)

        val imageView = ImageView(this)
        imageView.tag = signIndex
        imageView.id = View.generateViewId()
        imageView.x = signOnTheSchema.xCoordinate - (resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) / 2).toFloat()
        imageView.y = signOnTheSchema.yCoordinate - (resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight) / 2).toFloat()
        imageView.setImageDrawable(signOnTheSchema.drawable)
        val imageViewParams = ConstraintLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth).toInt(), resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight).toInt())
        imageView.layoutParams = imageViewParams


        val textView = TextView(this)
        textView.tag = "textView $signIndex"
        textView.id = View.generateViewId()
        textView.x = imageView.x + resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth) + 5
        textView.y = imageView.y
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
                        if (yDelta.absoluteValue > 1 || xDelta > 1) {
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
                            model.signs[v!!.tag as Int].xCoordinate = event.rawX
                            model.signs[v!!.tag as Int].yCoordinate = event.rawY
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
