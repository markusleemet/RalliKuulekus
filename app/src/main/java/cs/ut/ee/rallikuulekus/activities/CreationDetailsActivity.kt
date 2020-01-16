package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
import kotlinx.android.synthetic.main.activity_creation_details.*

class CreationDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_details)
    }

    fun openMainActivity(v: View) {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        val nameValue = text_input_name.text.toString()
        val descriptionValue = text_input_description.text.toString()
        mainActivityIntent.putExtra("name", nameValue)
        mainActivityIntent.putExtra("description", descriptionValue)
        startActivity(mainActivityIntent)
        finish()
    }
}
