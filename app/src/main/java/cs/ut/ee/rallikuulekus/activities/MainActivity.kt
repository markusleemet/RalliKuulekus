package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val SIGN_CLASS_SELECTION_ACTIVITY_CONSTANT = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_main)
        constraint_layout_main.setOnClickListener {
            openSignClassSelectionActivity(it)
        }
    }

    fun openSignClassSelectionActivity(v: View){
        val signClassSelectionIntent = Intent(this, SignClassSelectionActivity::class.java)
        startActivityForResult(signClassSelectionIntent, SIGN_CLASS_SELECTION_ACTIVITY_CONSTANT)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }
}
