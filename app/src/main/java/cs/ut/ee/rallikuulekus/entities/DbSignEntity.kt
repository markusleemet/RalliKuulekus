package cs.ut.ee.rallikuulekus.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(foreignKeys = arrayOf(ForeignKey(entity = DbSchemaEntity::class, parentColumns = ["id"], childColumns = ["idSchema"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)))
data class DbSignEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "idSchema") val idSchema: Int,
    @ColumnInfo(name = "x") val xCoordinate: Float,
    @ColumnInfo(name = "y") val yCoordinate: Float,
    @ColumnInfo(name = "class") val rkClass: Int,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "rotation") val rotation: String
){}