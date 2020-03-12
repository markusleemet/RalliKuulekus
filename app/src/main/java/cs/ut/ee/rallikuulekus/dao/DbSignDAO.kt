package cs.ut.ee.rallikuulekus.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cs.ut.ee.rallikuulekus.entities.DbSignEntity

@Dao
interface DbSignDAO {
    @Insert
    fun addSign(vararg sign: DbSignEntity)

    @Query("SELECT * FROM DbSignEntity WHERE idSchema LIKE :id")
    fun getAllSignsFromSchema(id: Int): List<DbSignEntity>

}
