package ui.state

import androidx.lifecycle.ViewModel
import data.ui.CommentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import unilang.alias.i64
import unilang.type.copyAdd
import unilang.type.copyUnless

data class CommentListScreenState(
    val full: List<CommentData>,
    val conflict: List<CommentData>,
)

private fun sort(list: List<CommentData>) = list.sortedBy { it.createTime }.reversed()

class CommentListScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        CommentListScreenState(
            listOf(),
            listOf(),
        )
    )

    val state = mutState.asStateFlow()

    fun reset(full: List<CommentData>, conflict: List<CommentData>) {
        mutState.value = CommentListScreenState(
            sort(full),
            sort(conflict),
        )
    }

    fun resetConflict(conflict: List<CommentData>) {
        mutState.value = CommentListScreenState(
            sort(mutState.value.full),
            sort(conflict),
        )
    }

    fun addConflict(data: CommentData) {
        val full = mutState.value.full.copyAdd(data)
        val conflict = mutState.value.conflict.copyAdd(data)
        mutState.value = CommentListScreenState(
            sort(full),
            sort(conflict),
        )
    }

    fun update(data: CommentData) {
        val full = mutState.value.full.copyUnless { it.id == data.id }
        full.add(data)
        val conflict = mutState.value.conflict.copyUnless { it.id == data.id }
        conflict.add(data)
        mutState.value = CommentListScreenState(
            sort(full),
            sort(conflict),
        )
    }

    fun remove(id: i64) {
        val full = mutState.value.full.copyUnless { it.id == id }
        val conflict = mutState.value.conflict.copyUnless { it.id == id }
        mutState.value = CommentListScreenState(
            sort(full),
            sort(conflict),
        )
    }
}