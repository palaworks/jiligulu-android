package component.data

import java.util.*
import unilang.alias.*

data class PostData(
    val id: i64, val title: String, val body: String, val createTime: Date, val modifyTime: Date
)
