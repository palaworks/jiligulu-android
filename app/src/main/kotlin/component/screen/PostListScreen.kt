package component.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.CardList
import component.card.PostCard
import data.db.LocalPostDbSingleton
import data.grpc.CommentServiceSingleton
import data.grpc.PostServiceSingleton
import data.ui.PostData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.rememberMutStateOf
import ui.state.PostListScreenViewModel
import ui.state.PostListScreenViewModelSingleton
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostListScreen(
    contentPadding: PaddingValues,
    navToPostEdit: (i64) -> Unit,
    navToCommentCreate: (i64) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    val viewModel = PostListScreenViewModelSingleton()
    val uiState by viewModel.state.collectAsState()

    suspend fun load() {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val local = dao.getAll()

        val service = PostServiceSingleton(ctx)
        if (service.isEmpty) {
            showSnackBar("Setting missing: please config your app first.")
            viewModel.reset(local, listOf())
            return
        }

        val remoteIdSha256Map = service.get().getAllSha256()
        if (remoteIdSha256Map.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local, listOf())
            return
        }

        val localOnly = mutableListOf<PostData>()
        val dataDiff = mutableListOf<PostData>()

        local.forEach { data ->
            val localSha256 = data.sha256()
            val remoteSha256 = remoteIdSha256Map.get()[data.id]

            if (remoteSha256 == null)
                localOnly.add(data)//local only
            else {
                remoteIdSha256Map.get().remove(data.id)//remove intersection
                if (localSha256 != remoteSha256)
                    dataDiff.add(data)//data diff
            }
        }

        val remoteOnly =
            service.get().getSome(remoteIdSha256Map.get().keys.toList())//add remote only

        if (remoteOnly.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local, listOf())
            return
        }

        viewModel.reset(
            local + remoteOnly.get(),//full
            localOnly + remoteOnly.get() + dataDiff//conflict
        )
    }

    val coroutineScope = rememberCoroutineScope()
    fun deleteLocal(id: i64) = coroutineScope.launch {
        withContext(Dispatchers.IO) {
            val dao = LocalPostDbSingleton(ctx).localPostDao()
            dao.delete(id)
        }
        load()
    }

    Column(
        FillMaxSizeModifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            !uiState.initialized,
            uiState.full,
            doRefresh = ::load,
            render = { data ->
                val id = data.id
                PostCard(
                    { navToPostEdit(id) },
                    { navToCommentCreate(id) },
                    afterConflictResolved = { coroutineScope.launch { load() } },
                    showSnackBar,
                    doDelete = { deleteLocal(id) },
                    data,
                    uiState.conflict.any { it.id == id },
                )
            }
        )
    }
}
