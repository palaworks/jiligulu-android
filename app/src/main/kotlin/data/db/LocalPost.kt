package data.db

import android.content.Context
import androidx.room.*
import data.db.converter.DateConverter
import data.ui.PostData
import unilang.alias.i64
import java.util.*

@Dao
interface LocalPostDao {
    @Query("SELECT * FROM local_post WHERE post_id = (:id)")
    fun maybe(id: i64): PostData?

    @Query("SELECT * FROM local_post WHERE post_id = (:id)")
    fun getOne(id: i64): PostData

    @Query("SELECT * FROM local_post")
    fun getAll(): List<PostData>

    @Insert
    fun insert(data: PostData)

    @Update
    fun update(data: PostData)

    @Query("DELETE FROM local_post WHERE post_id = (:id)")
    fun delete(id: i64)
}

@TypeConverters(DateConverter::class)
@Database(entities = [PostData::class], version = 1)
abstract class LocalPostDatabase : RoomDatabase() {
    abstract fun localPostDao(): LocalPostDao

    companion object {
        private var INSTANCE: LocalPostDatabase? = null
        fun getDatabase(ctx: Context): LocalPostDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            ctx,
                            LocalPostDatabase::class.java,
                            "local_post_database"
                        )
                            //TODO remove this for better performance
                            .allowMainThreadQueries()
                            .build()
                }
            }

            return INSTANCE!!
        }
    }
}
