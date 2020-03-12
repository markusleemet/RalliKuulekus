package cs.ut.ee.rallikuulekus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.entities.DbSchemaEntity
import cs.ut.ee.rallikuulekus.entities.Sign

class LoadAdapter(context: Context, val data: ArrayList<DbSchemaEntity>): ArrayAdapter<DbSchemaEntity>(context, 0, data){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.table_row_at_schema_load, parent, false)
        }
        val currentSign: DbSchemaEntity = data[position]

        val nameTextView: TextView = view!!.findViewById(R.id.text_view_load_name)
        val dateTextView: TextView = view.findViewById(R.id.text_view_load_date)
        val descriptionTextView: TextView = view.findViewById(R.id.text_view_load_description)

        nameTextView.text = if (currentSign.name != "") currentSign.name else "*Nimi puudub"
        view.tag = currentSign.id


        dateTextView.text = currentSign.date
        descriptionTextView.text = if (currentSign.description != "")  currentSign.description else "*Kirjeldus puudub"

        return view
    }
}