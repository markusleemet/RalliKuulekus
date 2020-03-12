package cs.ut.ee.rallikuulekus.viewModels

import android.app.Application
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import cs.ut.ee.rallikuulekus.database.SchemaDataBase
import cs.ut.ee.rallikuulekus.entities.*
import cs.ut.ee.rallikuulekus.functions.generateSignList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class SignsViewModel(application: Application) : AndroidViewModel(application) {
    var signs = ArrayList<SignOnTheSchema>()
    var id: Int = -1
    var name: String = ""
    var description: String = ""
    var schemaDate: String = ""
    lateinit var db: SchemaDataBase


    init {
        db = Room.databaseBuilder(
            application,
            SchemaDataBase::class.java, "schema-db"
        ).fallbackToDestructiveMigration().build()
    }



    fun addSignToSigns(sign: SignOnTheSchema){
        signs.add(sign)
        Log.i("viewModel", "signs count: ${signs.count()}")
    }

    fun getLastAddedSign(): SignOnTheSchema{
        return signs.last()
    }

    fun saveNewSchemaToDatabase() {
        val dbSchemaEntity = DbSchemaEntity(id, name, description, schemaDate)

        val currentSchemaWithThisId = db.DbSchemaDAO().getSchemaWithId(id)
        if (currentSchemaWithThisId != null) {
            Log.i("database", "deleting id: $id")
            db.DbSchemaDAO().deleteSchemaWithId(id)
        }

        db.DbSchemaDAO().insertSchema(dbSchemaEntity)
        signs.forEach {
            val dbSignEntity = DbSignEntity(0, id, it.xCoordinate, it.yCoordinate, it.rkClass, it.position, it.rotation.name)
            db.DbSignDAO().addSign(dbSignEntity)

        }

    }

    fun initNewSchema(aName: String, aDescription: String) {
        name = aName
        description = aDescription
        schemaDate = getDate()
        id = db.DbSchemaDAO().countAllSchemas() + 1
    }

    fun initExistingSchema(id: Int) {
        thread {
            val schema = db.DbSchemaDAO().getSchemaWithId(id)
            name = schema!!.name
            description = schema.description
            this.id = id
            schemaDate = schema.date
        }
    }

    fun getSignsToPutOnScreen(id: Int): ArrayList<SignOnTheSchema> {
        val listToReturn = ArrayList<SignOnTheSchema>()

        val dbSigns = db.DbSignDAO().getAllSignsFromSchema(id) as ArrayList<DbSignEntity>
        val signs1 = generateSignList(1, getApplication())
        val signs2 = generateSignList(2, getApplication())
        val signs3 = generateSignList(3, getApplication())
        val signs4 = generateSignList(4, getApplication())
        dbSigns.forEach {

            val sign = when (it.rkClass) {
                1 -> signs1[it.position]
                2 -> signs2[it.position]
                3 -> signs3[it.position]
                4 -> signs4[it.position]
                else -> null
            }

            val signForScreen: SignOnTheSchema = SignOnTheSchema(
                it.xCoordinate,
                it.yCoordinate,
                it.rkClass,
                sign!!.drawable,
                sign.heading,
                sign.description,
                it.position,
                SignRotation.valueOf(it.rotation)
            )
            listToReturn.add(signForScreen)
        }
        signs = listToReturn
        return listToReturn
    }

    private fun getDate(): String{
        val date: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}