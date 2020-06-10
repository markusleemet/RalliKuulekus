package cs.ut.ee.rallikuulekus.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(ForeignKey(entity = DbSchemaEntity::class, parentColumns = ["id"], childColumns = ["idSchema"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)))
data class DbStartEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name= "xCoordinate") val xCoordinate: Float,
    @ColumnInfo(name= "yCoordinate") val yCoordinate: Float,
    @ColumnInfo(name= "idSchema") val schemaId: Int
)