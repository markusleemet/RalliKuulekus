package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.fragments.FragmentMenu
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
        val menuFragment = FragmentMenu()
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

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
