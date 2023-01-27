package data.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import unilang.alias.i64
import unilang.hash.sha256
import java.util.*

@Entity(tableName = "local_post")
data class PostData(
    @PrimaryKey @ColumnInfo(name = "post_id") val id: i64,
    @ColumnInfo(name = "post_title") val title: String,
    @ColumnInfo(name = "post_body") val body: String,
    @ColumnInfo(name = "post_create_time") val createTime: Date,
    @ColumnInfo(name = "post_modify_time") val modifyTime: Date
)

fun PostData.sha256() = (this.title + this.body).sha256()
