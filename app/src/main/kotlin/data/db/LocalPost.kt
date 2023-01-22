package data.db

import java.util.Date
import androidx.room.*
import unilang.alias.i64
import android.content.Context
import data.db.converter.DateConverter

@Entity(tableName = "local_post")
data class LocalPost(
    @PrimaryKey val id: i64,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "create_time") val createTime: Date,
    @ColumnInfo(name = "modify_time") val modifyTime: Date,
)

@Dao
interface LocalPostDao {
    @Query("SELECT * FROM local_post WHERE id = (:id)")
    fun maybe(id: i64): LocalPost?

    @Query("SELECT * FROM local_post")
    fun getAll(): List<LocalPost>

    @Insert
    fun insert(localPost: LocalPost)

    @Update
    fun update(localPost: LocalPost)

    @Delete
    fun delete(localPost: LocalPost)
}

@TypeConverters(DateConverter::class)
@Database(entities = [LocalPost::class], version = 1)
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
