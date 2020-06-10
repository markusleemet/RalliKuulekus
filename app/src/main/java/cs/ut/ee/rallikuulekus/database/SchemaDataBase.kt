package cs.ut.ee.rallikuulekus.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cs.ut.ee.rallikuulekus.dao.DbFinishSignDAO
import cs.ut.ee.rallikuulekus.dao.DbSchemaDAO
import cs.ut.ee.rallikuulekus.dao.DbSignDAO
import cs.ut.ee.rallikuulekus.dao.DbStartSignDAO
import cs.ut.ee.rallikuulekus.entities.DbFinishEntity
import cs.ut.ee.rallikuulekus.entities.DbSchemaEntity
import cs.ut.ee.rallikuulekus.entities.DbSignEntity
import cs.ut.ee.rallikuulekus.entities.DbStartEntity

@Database(entities = arrayOf(DbSchemaEntity::class, DbSignEntity::class, DbStartEntity::class, DbFinishEntity::class), version = 11, exportSchema = false)
abstract class SchemaDataBase : RoomDatabase() {
    abstract fun DbSchemaDAO(): DbSchemaDAO
    abstract fun DbSignDAO(): DbSignDAO
    abstract fun DbStartSignDAO(): DbStartSignDAO
    abstract fun DbFinishSignDAO(): DbFinishSignDAO

}
