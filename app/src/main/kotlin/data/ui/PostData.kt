package data.ui

import data.db.LocalPost
import unilang.alias.i64
import unilang.hash.sha256
import java.util.*

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
