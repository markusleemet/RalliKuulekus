package cs.ut.ee.rallikuulekus.activities

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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.fragments.FragmentMenu
import cs.ut.ee.rallikuulekus.functions.CapturePhotoUtils
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import cs.ut.ee.rallikuulekus.views.Grid
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), FragmentMenu.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_main)

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
                        supportFragmentManager.popBackStack()
                    } else {
                        openSignClassSelectionActivity(event.x, event.y)
                    }
                }
            }
            true
        }

        fragment_container_menu.setOnTouchListener { v, event ->
            true
        }
    }

    fun openSignClassSelectionActivity(xCoordinate: Float, yCoordinate: Float){
        val signClassSelectionIntent = Intent(this, SignClassSelectionActivity::class.java)
        signClassSelectionIntent.putExtra("x", xCoordinate)
        signClassSelectionIntent.putExtra("y", yCoordinate)
        startActivity(signClassSelectionIntent)
    }

    fun openMenuFragment(v: View){
        val nameValue = intent.getStringExtra("name")!!
        val descriptionValue = intent.getStringExtra("description")!!
        val menuFragment = FragmentMenu.newInstance(nameValue, descriptionValue)
        val supportFragmentManager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_menu, menuFragment)
        fragmentTransaction.addToBackStack("menu")
        fragmentTransaction.commit()
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
}
