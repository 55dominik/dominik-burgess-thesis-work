package gb.dom55.bme.smarthome.model.scenes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scene_id_table")
data class SceneItem (
    @PrimaryKey @ColumnInfo(name = "sceneid") val sceneId: String,
    @ColumnInfo(name = "userid") var userId: String,
    @ColumnInfo(name = "scenename")  var name: String,
    @ColumnInfo(name = "isactive") var isActive: Boolean = false,
    @ColumnInfo(name = "positionrv") var positionInRV: Int? = null
)