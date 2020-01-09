package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI

class SignClassSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_class_selection)
    }


    fun openSignSelectionActivity(v: View) {
        val rkClass = when(v.id){
            R.id.button_rk1 -> 1
            R.id.button_rk2 -> 2
            R.id.button_rk3 -> 3
            R.id.button_rk4 -> 4
            else -> -1
        }
        val signSelectionIntent = Intent(this, SignSelectionActivity::class.java)
        signSelectionIntent.putExtra("class", rkClass)
        signSelectionIntent.putExtra("x", intent.getFloatExtra("x", -1f))
        signSelectionIntent.putExtra("y", intent.getFloatExtra("y", -1f))
        startActivity(signSelectionIntent)
    }
}
