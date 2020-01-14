package cs.ut.ee.rallikuulekus.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.adapters.SignAdapter
import cs.ut.ee.rallikuulekus.functions.generateSignList
import kotlinx.android.synthetic.main.activity_sign_selection.*

class SignSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_selection)


        val rkClass = intent.getIntExtra("class", -1)
        val signsList = generateSignList(rkClass, this)
        val adapter = SignAdapter(this, signsList)
        list_view_signs.adapter = adapter
        list_view_signs.setOnItemClickListener { parent, view, position, id ->
            val resultIntent = Intent()
            resultIntent.putExtra("class", rkClass)
            resultIntent.putExtra("position", position)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
