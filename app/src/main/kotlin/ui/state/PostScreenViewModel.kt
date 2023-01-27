package ui.state

import androidx.lifecycle.ViewModel
import data.ui.PostData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PostScreenState(
    val full: List<PostData>,
    val conflict: List<PostData>,
)

class PostScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        PostScreenState(
            listOf(),
            listOf(),
        )
    )

    val state = mutState.asStateFlow()

    fun reset(full: List<PostData>, conflict: List<PostData>) {
        mutState.value = PostScreenState(
            full,
            conflict,
        )
    }

    fun resetConflict(conflict: List<PostData>) {
        mutState.value = PostScreenState(
            mutState.value.full,
            conflict,
        )
    }
}
