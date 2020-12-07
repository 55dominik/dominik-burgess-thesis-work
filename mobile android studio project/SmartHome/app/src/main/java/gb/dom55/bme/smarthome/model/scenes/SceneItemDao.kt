package gb.dom55.bme.smarthome.model.scenes

import androidx.room.*

@Dao
interface SceneItemDao {

    @Query("SELECT * FROM scene_id_table WHERE userid = :uid")
    fun getAllScenes(uid: String): MutableList<SceneItem>

    @Insert
    fun insert(sceneItem: SceneItem): Long

    @Query("SELECT MAX(positionrv) FROM scene_id_table WHERE userid = :uid")
    fun getLargestPosition(uid: String) : Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun reOrder(scenes: MutableList<SceneItem>)

    @Update
    fun update(sceneItem: SceneItem)

    @Delete
    fun delete(sceneItem: SceneItem)
}