package data.db

import android.content.Context
import androidx.room.*
import data.db.converter.DateConverter
import data.ui.CommentData
import unilang.alias.i64
import java.util.*

@Dao
interface LocalCommentDao {
    @Query("SELECT * FROM local_comment WHERE comment_id = (:id)")
    fun maybe(id: i64): CommentData?

    @Query("SELECT * FROM local_comment WHERE comment_id = (:id)")
    fun getOne(id: i64): CommentData

    @Query("SELECT * FROM local_comment")
    fun getAll(): List<CommentData>

    @Insert
    fun insert(data: CommentData)

    @Update
    fun update(data: CommentData)

    @Query("DELETE FROM local_comment WHERE comment_id = (:id)")
    fun delete(id: i64)
}

@TypeConverters(DateConverter::class)
@Database(entities = [CommentData::class], version = 1)
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
