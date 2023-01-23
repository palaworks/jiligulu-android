package data.ui

import data.db.LocalPost
import java.util.*
import unilang.alias.*
import unilang.hash.sha256
import unilang.time.Iso8601
import unilang.time.toDate

data class CommentData(
    val id: i64,
    val body: String,
    val createTime: Date
)

fun CommentData(localPost: LocalPost) =
    CommentData(
        localPost.id,
        localPost.body,
        localPost.createTime
    )

fun CommentData.sha256() = this.body.sha256()
