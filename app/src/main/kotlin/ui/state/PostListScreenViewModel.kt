package ui.state

import androidx.lifecycle.ViewModel
import data.ui.PostData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import unilang.alias.i64
import unilang.type.copyAdd
import unilang.type.copyUnless

typealias PostDataWithConflictMark = Pair<PostData, Boolean>

data class PostListScreenState(
    val list: List<PostDataWithConflictMark>,
    val needReload: Boolean
)

private fun sort(list: List<PostDataWithConflictMark>) =
    list.sortedBy { it.first.createTime }.reversed()

class PostListScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        PostListScreenState(
            listOf(),
            true
        )
    )

    val state = mutState.asStateFlow()

    fun reset(list: List<PostDataWithConflictMark>) {
        mutState.value = PostListScreenState(
            sort(list),
            false
        )
    }

    fun add(data: PostDataWithConflictMark) {
        val new = mutState.value.list.copyAdd(data)
        reset(new)
    }

    fun remove(id: i64) {
        val new = mutState.value.list
            .copyUnless { it.first.id == id }
        reset(new)
    }

    fun removeThenAdd(data: PostDataWithConflictMark) {
        val new = mutState.value.list
            .copyUnless { it.first.id == data.first.id }
        new.add(data)
        reset(new)
    }

    fun setNeedReload() {
        mutState.value = mutState.value.copy(needReload = true)
    }
}

object PostListScreenViewModelSingleton {
    private var viewModel = PostListScreenViewModel()
    fun reset() = viewModel.reset(listOf())
    operator fun invoke() = viewModel
}
