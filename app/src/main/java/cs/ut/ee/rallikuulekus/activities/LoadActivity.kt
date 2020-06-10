package cs.ut.ee.rallikuulekus.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.room.Room
import cs.ut.ee.rallikuulekus.R
import cs.ut.ee.rallikuulekus.adapters.LoadAdapter
import cs.ut.ee.rallikuulekus.database.SchemaDataBase
import cs.ut.ee.rallikuulekus.entities.DbSchemaEntity
import kotlinx.android.synthetic.main.activity_load.*
import kotlin.concurrent.thread

class LoadActivity : AppCompatActivity() {
    private lateinit var db: SchemaDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        db = Room.databaseBuilder(
            application,
            SchemaDataBase::class.java, "schema-db"
        ).fallbackToDestructiveMigration().build()

        thread {
            val schemas = db.DbSchemaDAO().getAllSchemas().reversed()
            val schemasAsList = schemas.toMutableList() as ArrayList<DbSchemaEntity>
            val adapter = LoadAdapter(this, schemasAsList)
            runOnUiThread {
                list_view_load_signs.adapter = adapter
                list_view_load_signs.setOnItemClickListener { parent, view, position, id ->
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.putExtra("id", view.tag as Int)
                    startActivity(mainActivityIntent)
                    finish()
                }
            }
        }
    }
}
