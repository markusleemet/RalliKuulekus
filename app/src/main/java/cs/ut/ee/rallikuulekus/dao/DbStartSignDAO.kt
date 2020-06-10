package cs.ut.ee.rallikuulekus.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cs.ut.ee.rallikuulekus.entities.DbStartEntity

@Dao
interface DbStartSignDAO{
    @Insert
    fun addStartSign(vararg sign: DbStartEntity)

    @Query("SELECT * FROM DbStartEntity WHERE idSchema LIKE :id")
    fun getStartSignWithSchemaId(id: Int): DbStartEntity
}