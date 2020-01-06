package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI


class OpeningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI(window)
        setContentView(R.layout.activity_opening)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI(window)
    }

    fun openCreationActivity(v: View){
        val creationDetailsActivityIntent = Intent(this, CreationDetailsActivity::class.java)
        startActivity(creationDetailsActivityIntent)
    }

}
