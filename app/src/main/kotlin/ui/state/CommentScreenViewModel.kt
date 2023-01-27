package ui.state

import androidx.lifecycle.ViewModel
import data.ui.CommentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CommentScreenState(
    val full: List<CommentData>,
    val conflict: List<CommentData>,
)

class CommentScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        CommentScreenState(
            listOf(),
            listOf(),
        )
    )

    val state = mutState.asStateFlow()

    fun reset(full: List<CommentData>, conflict: List<CommentData>) {
        mutState.value = CommentScreenState(
            full,
            conflict,
        )
    }

    fun resetConflict(conflict: List<CommentData>) {
        mutState.value = CommentScreenState(
            mutState.value.full,
            conflict,
        )
    }
}