package data.ui

import unilang.alias.i64

sealed class PostEditMode {
    object Create : PostEditMode()

    data class Edit(
        val id: i64
    ) : PostEditMode()
}
