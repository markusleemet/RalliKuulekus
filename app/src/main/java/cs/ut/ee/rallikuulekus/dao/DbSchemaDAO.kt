package cs.ut.ee.rallikuulekus.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cs.ut.ee.rallikuulekus.entities.DbSchemaEntity

@Dao
interface DbSchemaDAO {
    @Query("SELECT COUNT(*) FROM DbSchemaEntity")
    fun countAllSchemas(): Int

    @Query("SELECT * FROM DbSchemaEntity")
    fun getAllSchemas(): List<DbSchemaEntity>

    @Query("SELECT * FROM DbSchemaEntity WHERE id LIKE :id")
    fun getSchemaWithId(id: Int): DbSchemaEntity?

    @Query("DELETE FROM DbSchemaEntity WHERE id LIKE :id")
    fun deleteSchemaWithId(id: Int)

    @Insert()
    fun insertSchema(vararg dbSchemaEntity: DbSchemaEntity)
}