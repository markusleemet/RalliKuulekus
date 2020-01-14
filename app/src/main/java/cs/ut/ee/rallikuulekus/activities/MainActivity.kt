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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema
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
                constraint_layout_main.addView(grid)
                constraint_layout_main.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        constraint_layout_main.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        while (supportFragmentManager.backStackEntryCount != 0) {
                            Log.i("viewModel", "entry count: ${supportFragmentManager.backStackEntryCount}")
                            supportFragmentManager.popBackStackImmediate()
                        }
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

    private fun openMenuFragment(v: View){
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
        supportFragmentManager.popBackStack()
        constraint_layout_main.removeView(constraint_layout_main.findViewWithTag<LinearLayout>(signIndex))
        model.signs.removeAt(signIndex)
        refreshIndexes(signIndex)
    }

    private fun refreshIndexes(removedSignIndex: Int){
        for (view in constraint_layout_main.children) {
            if (view is LinearLayout) {
                val oldIndex = view.tag as Int
                if (oldIndex > removedSignIndex) {
                    val newIndex = oldIndex - 1
                    view.tag = newIndex
                    view.findViewWithTag<TextView>("linearLayoutTextView").text = (newIndex + 1).toString()
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
            Log.i("viewModel", "signOnTheSchema: ${signOnTheSchema}")
            model.addSignToSigns(signOnTheSchema)
            createViewGroupForSign(signOnTheSchema)
        }
        if (requestCode == CHANGE_SIGN_CONSTANT && resultCode == Activity.RESULT_OK) {
            val signIndex = data!!.getIntExtra("index", -1)
            val rkClass = data.getIntExtra("class", -1)
            val position = data.getIntExtra("position", -1)
            val linearLayout = constraint_layout_main.findViewWithTag<LinearLayout>(signIndex)
            val sign = generateSignList(rkClass, this)[position]
            linearLayout.findViewWithTag<ImageView>("linearLayoutImageView").setImageDrawable(sign.drawable)
            model.signs[signIndex].drawable = sign.drawable
            model.signs[signIndex].heading = sign.heading
            model.signs[signIndex].description = sign.description
        }
    }

    private fun createViewGroupForSign(signOnTheSchema: SignOnTheSchema){
        val signIndexOnTheScreen = model.signs.indexOf(signOnTheSchema) + 1
        val linearLayout = LinearLayout(this)
        val linearLayoutLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayout.layoutParams = linearLayoutLayoutParams
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.x = signOnTheSchema.xCoordinate
        linearLayout.y = signOnTheSchema.yCoordinate
        linearLayout.tag = model.signs.indexOf(signOnTheSchema)
        linearLayout.setOnTouchListener(object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            var drag = false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val xDelta = lastX - event!!.rawX
                        val yDelta = lastY - event!!.rawY
                        if (yDelta.absoluteValue > 1 || xDelta > 1) {
                            linearLayout.x = linearLayout.x - xDelta
                            linearLayout.y = linearLayout.y - yDelta
                            lastX = event!!.rawX
                            lastY = event!!.rawY
                            drag = true
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event!!.rawX
                        lastY = event!!.rawY
                        drag = false
                        Log.i("viewModel", "signOnTheSchema: ${model.signs[v!!.tag as Int]}")
                    }
                    MotionEvent.ACTION_UP -> {
                        if (drag == false) {
                            openEditFragment(linearLayout.tag as Int)
                        }else{
                            model.signs[linearLayout.tag as Int].xCoordinate = event.rawX
                            model.signs[linearLayout.tag as Int].yCoordinate = event.rawY
                        }
                    }
                }
                return true
            }
        })
        constraint_layout_main.addView(linearLayout)

        val imageView = ImageView(this)
        imageView.tag = "linearLayoutImageView"
        imageView.setImageDrawable(signOnTheSchema.drawable)
        val imageViewParams = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.signOnTheSchemaWidth).toInt(), resources.getDimensionPixelSize(R.dimen.signOnTheSchemaHeight).toInt())
        imageViewParams.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.imageViewMargin), 0)
        imageView.layoutParams = imageViewParams
        linearLayout.addView(imageView)

        val textView = TextView(this)
        textView.tag = "linearLayoutTextView"
        textView.text = signIndexOnTheScreen.toString()
        textView.textSize = resources.getDimension(R.dimen.signNumberTextSize)
        textView.setTextColor(Color.BLACK)
        linearLayout.addView(textView)
    }

}
