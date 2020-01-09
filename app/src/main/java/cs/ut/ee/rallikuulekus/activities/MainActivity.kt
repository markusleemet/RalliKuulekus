package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import cs.ut.ee.rallikuulekus.views.Grid
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

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
        constraint_layout_main.setOnClickListener {
            openSignClassSelectionActivity(it)
        }



    }

    fun openSignClassSelectionActivity(v: View){
        val signClassSelectionIntent = Intent(this, SignClassSelectionActivity::class.java)
        startActivity(signClassSelectionIntent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }


}
