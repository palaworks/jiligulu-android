package data.ui

import data.db.LocalComment
import java.util.*
import unilang.alias.*
import unilang.hash.sha256

data class CommentData(
    val id: i64,
    val body: String,
    val bindingId: i64,
    val isReply: Boolean,
    val createTime: Date
)

fun CommentData(localComment: LocalComment) =
    CommentData(
        localComment.id,
        localComment.body,
        localComment.bindingId,
        localComment.isReply,
        localComment.createTime
    )

fun CommentData.sha256() = this.body.sha256()
