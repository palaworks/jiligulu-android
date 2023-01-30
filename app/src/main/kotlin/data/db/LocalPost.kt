package data.db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import data.db.converter.DateConverter
import data.ui.PostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.alias.i64
import unilang.type.none
import unilang.type.some
import java.util.*

@Dao
interface LocalPostDao {
    @Query("SELECT * FROM local_post WHERE post_id = (:id)")
    suspend fun maybe(id: i64): PostData?

    @Query("SELECT * FROM local_post WHERE post_id = (:id)")
    suspend fun getOne(id: i64): PostData

    @Query("SELECT * FROM local_post")
    suspend fun getAll(): List<PostData>

    @Insert
    suspend fun insert(data: PostData)

    @Update
    suspend fun update(data: PostData)

    @Query("DELETE FROM local_post WHERE post_id = (:id)")
    suspend fun delete(id: i64)
}

@TypeConverters(DateConverter::class)
@Database(entities = [PostData::class], version = 1)
abstract class LocalPostDatabase : RoomDatabase() {
    abstract fun localPostDao(): LocalPostDao
}

object LocalPostDbSingleton {
    private var db = none<LocalPostDatabase>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend operator fun invoke(ctx: Context) =
        withContext(Dispatchers.IO) {
            if (db.isEmpty)
                synchronized(this) {
                    db = Room
                        .databaseBuilder(
                            ctx,
                            LocalPostDatabase::class.java,
                            "local_post_database"
                        )
                        .build()
                        .some()
                }

            db.get()
        }
}