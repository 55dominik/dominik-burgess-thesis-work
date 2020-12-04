package gb.dom55.bme.smarthome.model.scenes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(SceneItem::class), version = 1, exportSchema = false)
abstract class SceneDatabase : RoomDatabase() {
    abstract fun sceneIdDao() : SceneItemDao

    companion object {
        @Volatile
        private var INSTANCE: SceneDatabase? = null

        fun getDatabase(context: Context): SceneDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SceneDatabase::class.java,
                        "sceneid_database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance

            }
        }

    }

}