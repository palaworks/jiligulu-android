package data.ui

import data.db.LocalPost
import java.util.*
import unilang.alias.*
import unilang.hash.sha256
import unilang.time.Iso8601
import unilang.time.toDate

data class PostData(
    val id: i64,
    val title: String,
    val body: String,
    val createTime: Date,
    val modifyTime: Date
)

fun PostData(localPost: LocalPost) =
    PostData(
        localPost.id,
        localPost.title,
        localPost.body,
        localPost.createTime,
        localPost.modifyTime
    )

fun PostData.sha256() = (this.title + this.body).sha256()
