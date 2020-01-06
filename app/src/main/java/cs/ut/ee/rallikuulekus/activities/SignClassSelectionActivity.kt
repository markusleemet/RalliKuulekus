package cs.ut.ee.rallikuulekus.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI

class SignClassSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_sign_class_selection)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }
}
