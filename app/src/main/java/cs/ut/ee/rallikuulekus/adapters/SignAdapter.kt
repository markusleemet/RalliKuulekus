package cs.ut.ee.rallikuulekus.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.Sign
import cs.ut.ee.rallikuulekus.entities.SignOnTheSchema

class SignAdapter(context: Context, val data: ArrayList<Sign>): ArrayAdapter<Sign>(context, 0, data){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.table_row_at_sign_selection, parent, false)
        }
        val currentSign: Sign = data[position]

        val signImageView: ImageView = view!!.findViewById(R.id.image_view_sign_table_image)
        val heading: TextView = view.findViewById(R.id.text_view_sign_table_heading)
        val description: TextView = view.findViewById(R.id.text_view_sign_table_description)

        signImageView.setImageDrawable(currentSign.drawable)
        heading.text = currentSign.heading
        description.text = currentSign.description

        return view
    }
}