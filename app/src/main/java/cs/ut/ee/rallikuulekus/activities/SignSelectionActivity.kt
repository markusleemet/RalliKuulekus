package cs.ut.ee.rallikuulekus.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.adapters.SignAdapter
import cs.ut.ee.rallikuulekus.entities.Sign
import cs.ut.ee.rallikuulekus.functions.generateSignList
import cs.ut.ee.rallikuulekus.functions.hideSystemUI
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
            doSomethingWithTheSelectedSign(view)
        }
    }

    private fun doSomethingWithTheSelectedSign(view: View){
        //example
        Log.i("doSomething","heading: ${view.findViewById<TextView>(R.id.text_view_sign_table_heading).text}")
        Log.i("doSomething","description: ${view.findViewById<TextView>(R.id.text_view_sign_table_description).text}")
        Log.i("doSomething","image_view: ${view.findViewById<ImageView>(R.id.image_view_sign_table_image)}")
        Log.i("doSomething","x coordinate: ${intent.getFloatExtra("x", -1f)}")
        Log.i("doSomething","y coordinate: ${intent.getFloatExtra("y", -1f)}")
    }
}
