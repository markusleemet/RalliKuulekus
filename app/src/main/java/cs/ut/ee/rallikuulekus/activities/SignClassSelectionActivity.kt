package cs.ut.ee.rallikuulekus.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cs.ut.ee.rallikuulekus.R

class SignClassSelectionActivity : AppCompatActivity() {
    private val NEW_SIGN_SELECTION_CONSTANT = 12345
    private val EXISTING_SIGN_CHANGING_CONSTANT = 12346

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
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            startActivityForResult(signSelectionIntent, NEW_SIGN_SELECTION_CONSTANT)
        }else{
            startActivityForResult(signSelectionIntent, EXISTING_SIGN_CHANGING_CONSTANT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_SIGN_SELECTION_CONSTANT && resultCode == Activity.RESULT_OK) {
            val resultIntent = Intent()
            resultIntent.putExtra("class", data!!.getIntExtra("class", -1))
            resultIntent.putExtra("position", data.getIntExtra("position", -1))
            resultIntent.putExtra("x", intent.getFloatExtra("x", -1f))
            resultIntent.putExtra("y", intent.getFloatExtra("y", -1f))
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        if (requestCode == EXISTING_SIGN_CHANGING_CONSTANT && resultCode == Activity.RESULT_OK) {
            val resultIntent = Intent()
            resultIntent.putExtra("class", data!!.getIntExtra("class", -1))
            resultIntent.putExtra("position", data.getIntExtra("position", -1))
            resultIntent.putExtra("index", intent!!.getIntExtra("index", -1))
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
