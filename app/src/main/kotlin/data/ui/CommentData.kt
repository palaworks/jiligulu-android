package data.ui

import data.db.LocalComment
import unilang.alias.i64
import unilang.hash.sha256
import java.util.*

data class CommentData(
    val id: i64,
    val body: String,
    val bindingId: i64,
    val isReply: Boolean,
    val createTime: Date,
    val modifyTime: Date
)

fun CommentData(localComment: LocalComment) =
    CommentData(
        localComment.id,
        localComment.body,
        localComment.bindingId,
        localComment.isReply,
        localComment.createTime,
        localComment.modifyTime
    )

fun CommentData.sha256() = this.body.sha256()
