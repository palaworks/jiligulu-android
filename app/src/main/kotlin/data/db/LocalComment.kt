package data.db

import java.util.Date
import androidx.room.*
import unilang.alias.i64
import android.content.Context
import data.db.converter.DateConverter

@Entity(tableName = "local_comment")
data class LocalComment(
    @PrimaryKey val id: i64,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "create_time") val createTime: Date,
)

@Dao
interface LocalCommentDao {
    @Query("SELECT * FROM local_comment WHERE id = (:id)")
    fun maybe(id: i64): LocalComment?

    @Query("SELECT * FROM local_comment")
    fun getAll(): List<LocalComment>

    @Insert
    fun insert(localComment: LocalComment)

    @Update
    fun update(localComment: LocalComment)

    @Delete
    fun delete(localComment: LocalComment)
}

@TypeConverters(DateConverter::class)
@Database(entities = [LocalComment::class], version = 1)
abstract class LocalCommentDatabase : RoomDatabase() {
    abstract fun localCommentDao(): LocalCommentDao

    companion object {
        private var INSTANCE: LocalCommentDatabase? = null
        fun getDatabase(ctx: Context): LocalCommentDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            ctx,
                            LocalCommentDatabase::class.java,
                            "local_comment_database"
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
