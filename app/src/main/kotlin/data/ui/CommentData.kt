package data.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import unilang.alias.i64
import unilang.hash.sha256
import java.util.*

@Entity(tableName = "local_comment")
data class CommentData(
    @PrimaryKey @ColumnInfo(name = "comment_id") val id: i64,
    @ColumnInfo(name = "comment_body") val body: String,
    @ColumnInfo(name = "comment_binding_id") val bindingId: i64,
    @ColumnInfo(name = "comment_is_reply") val isReply: Boolean,
    @ColumnInfo(name = "comment_create_time") val createTime: Date,
    @ColumnInfo(name = "comment_modify_time") val modifyTime: Date
)

fun CommentData.sha256() = this.body.sha256()
