package cs.ut.ee.rallikuulekus.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cs.ut.ee.rallikuulekus.dao.DbSchemaDAO
import cs.ut.ee.rallikuulekus.dao.DbSignDAO
import cs.ut.ee.rallikuulekus.entities.DbSchemaEntity
import cs.ut.ee.rallikuulekus.entities.DbSignEntity

@Database(entities = arrayOf(DbSchemaEntity::class, DbSignEntity::class), version = 10, exportSchema = false)
abstract class SchemaDataBase : RoomDatabase() {
    abstract fun DbSchemaDAO(): DbSchemaDAO
    abstract fun DbSignDAO(): DbSignDAO

}
