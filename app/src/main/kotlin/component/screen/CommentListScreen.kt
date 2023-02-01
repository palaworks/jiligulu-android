package component.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import component.CardList
import component.TryPullDownInfo
import component.card.CommentCard
import data.db.LocalCommentDbSingleton
import data.grpc.CommentServiceSingleton
import data.ui.CommentData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.rememberMutStateOf
import ui.state.CommentListScreenViewModel
import ui.state.CommentListScreenViewModelSingleton
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*
import kotlin.coroutines.coroutineContext

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentListScreen(
    contentPadding: PaddingValues,
    navToCommentEdit: (i64) -> Unit,
    navToCommentCreate: (i64) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    val viewModel = CommentListScreenViewModelSingleton()
    val uiState by viewModel.state.collectAsState()

    suspend fun load() {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        val local = dao.getAll()

        val service = CommentServiceSingleton(ctx)
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

        val localOnly = mutableListOf<CommentData>()
        val dataDiff = mutableListOf<CommentData>()

        local.forEach { data ->
            val localSha256 = data.sha256()
            val remoteSha256 = remoteIdSha256Map.get()[data.id]

            if (remoteSha256 == null)
                localOnly.add(data)//local only comment
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
            val dao = LocalCommentDbSingleton(ctx).localCommentDao()
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
                CommentCard(
                    { navToCommentEdit(id) },
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
