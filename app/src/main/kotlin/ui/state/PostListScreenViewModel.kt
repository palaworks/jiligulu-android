package ui.state

import androidx.lifecycle.ViewModel
import data.ui.PostData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import unilang.alias.i64
import unilang.type.copyAdd
import unilang.type.copyUnless

data class PostListScreenState(
    val full: List<PostData>,
    val conflict: List<PostData>,
    val initialized: Boolean
)

private fun sort(list: List<PostData>) = list.sortedBy { it.createTime }.reversed()

class PostListScreenViewModel : ViewModel() {
    private val mutState = MutableStateFlow(
        PostListScreenState(
            listOf(),
            listOf(),
            false,
        )
    )

    val state = mutState.asStateFlow()

    fun reset(full: List<PostData>, conflict: List<PostData>) {
        mutState.value = PostListScreenState(
            sort(full),
            sort(conflict),
            true,
        )
    }

    fun addConflict(data: PostData) {
        val full = mutState.value.full.copyAdd(data)
        val conflict = mutState.value.conflict.copyAdd(data)
        reset(
            full,
            conflict,
        )
    }

    fun update(data: PostData) {
        val full = mutState.value.full.copyUnless { it.id == data.id }
        full.add(data)
        val conflict = mutState.value.conflict.copyUnless { it.id == data.id }
        conflict.add(data)
        reset(
            full,
            conflict,
        )
    }

    fun remove(id: i64) {
        val full = mutState.value.full.copyUnless { it.id == id }
        val conflict = mutState.value.conflict.copyUnless { it.id == id }
        reset(
            full,
            conflict,
        )
    }
}

object PostListScreenViewModelSingleton {
    private var viewModel = PostListScreenViewModel()
    fun reset() = viewModel.reset(listOf(), listOf())
    operator fun invoke() = viewModel
}
