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
import component.card.PostCard
import data.db.LocalPostDbSingleton
import data.grpc.PostServiceSingleton
import data.ui.sha256
import kotlinx.coroutines.launch
import ui.FillMaxSizeModifier
import ui.state.PostListScreenViewModelSingleton
import unilang.alias.i64
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostListScreen(
    contentPadding: PaddingValues,
    navToPostEdit: (i64) -> Unit,
    navToCommentCreate: (i64) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    val viewModel = PostListScreenViewModelSingleton()

    suspend fun load() {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val local = dao.getAll()

        val service = PostServiceSingleton(ctx)
        if (service.isEmpty) {
            showSnackBar("Setting missing: please config your app first.")
            viewModel.reset(local.map { Pair(it, false) })
            return
        }

        val remoteIdSha256Map = service.get().getAllSha256()
        if (remoteIdSha256Map.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local.map { Pair(it, false) })
            return
        }

        val remoteOnlyIdList = remoteIdSha256Map.get().keys.toMutableList()

        //TODO poor naming
        val localWithConflictMark = local.map { data ->
            val localSha256 = data.sha256()
            val remoteSha256 = remoteIdSha256Map.get()[data.id]

            if (remoteSha256 == null)
                Pair(data, true)//local only
            else {
                remoteOnlyIdList.remove(data.id)//remove intersection
                Pair(data, localSha256 != remoteSha256)
            }
        }

        val remoteOnly = service.get()
            .getSome(remoteOnlyIdList)//get remote only

        if (remoteOnly.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local.map { Pair(it, false) })
            return
        }

        viewModel.reset(localWithConflictMark + remoteOnly.get().map { Pair(it, true) })
    }

    val coroutineScope = rememberCoroutineScope()
    fun deleteLocal(id: i64) = coroutineScope.launch {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        dao.delete(id)
        load()
    }

    val uiState by viewModel.state.collectAsState()
    Column(
        FillMaxSizeModifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            uiState.needReload,
            uiState.list,
            doRefresh = ::load,
            render = { (data, hasConflict) ->
                val id = data.id
                PostCard(
                    { navToPostEdit(id) },
                    { navToCommentCreate(id) },
                    afterConflictResolved = { coroutineScope.launch { load() } },
                    showSnackBar,
                    doDelete = { deleteLocal(id) },
                    data,
                    hasConflict
                )
            }
        )
    }
}
