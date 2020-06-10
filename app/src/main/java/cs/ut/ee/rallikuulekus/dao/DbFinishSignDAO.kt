package cs.ut.ee.rallikuulekus.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cs.ut.ee.rallikuulekus.entities.DbFinishEntity

@Dao
interface DbFinishSignDAO{
    @Insert
    fun addFinishSign(vararg sign: DbFinishEntity)

    @Query("SELECT * FROM DbFinishEntity WHERE idSchema LIKE :id")
    fun getFinishSignWithSchemaId(id: Int): DbFinishEntity
}