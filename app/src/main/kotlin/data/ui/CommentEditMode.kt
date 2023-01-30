package data.ui

import unilang.alias.i64

sealed class CommentEditMode {
    data class Create(
        val bindingId: i64,
        val isReply: Boolean
    ) : CommentEditMode()

    data class Edit(
        val id: i64
    ) : CommentEditMode()
}