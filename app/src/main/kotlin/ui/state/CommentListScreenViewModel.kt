package ui.state

import androidx.lifecycle.ViewModel
import data.ui.CommentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import unilang.alias.i64
import unilang.type.copyAdd
import unilang.type.copyUnless

typealias CommentDataWithConflictMark = Pair<CommentData, Boolean>

data class CommentListScreenState(
    val list: List<CommentDataWithConflictMark>,
    val initialized: Boolean
)

private fun sort(list: List<Pair<CommentData, Boolean>>) =
    list.sortedBy { it.first.createTime }.reversed()

class CommentListScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        CommentListScreenState(
            listOf(),
            false
        )
    )

    val state = mutState.asStateFlow()

    fun reset(list: List<CommentDataWithConflictMark>) {
        mutState.value = CommentListScreenState(
            sort(list),
            true
        )
    }

    fun add(data: CommentDataWithConflictMark) {
        val new = mutState.value.list.copyAdd(data)
        reset(new)
    }

    fun remove(id: i64) {
        val new = mutState.value.list
            .copyUnless { it.first.id == id }
        reset(new)
    }

    fun removeThenAdd(data: CommentDataWithConflictMark) {
        val new = mutState.value.list
            .copyUnless { it.first.id == data.first.id }
        new.add(data)
        reset(new)
    }
}

object CommentListScreenViewModelSingleton {
    private var viewModel = CommentListScreenViewModel()
    fun reset() = viewModel.reset(listOf())
    operator fun invoke() = viewModel
}
