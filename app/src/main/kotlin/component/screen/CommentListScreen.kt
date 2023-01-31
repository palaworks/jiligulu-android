package component.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import component.CardList
import component.card.CommentCard
import data.db.LocalCommentDbSingleton
import data.grpc.CommentServiceSingleton
import data.ui.CommentData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.state.CommentListScreenViewModel
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentListScreen(
    contentPadding: PaddingValues,
    viewModel: CommentListScreenViewModel,
    navToCommentEdit: (i64) -> Unit,
    navToCommentCreate: (i64) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    suspend fun load() = withContext(Dispatchers.IO) {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        val service = CommentServiceSingleton(ctx).get()

        val local = dao.getAll()

        val remoteIdSha256Map = service.getAllSha256()
        if (remoteIdSha256Map.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local, listOf())
            return@withContext
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
            service.getSome(remoteIdSha256Map.get().keys.toList())//add remote only

        if (remoteOnly.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local, listOf())
            return@withContext
        }

        viewModel.reset(
            local + remoteOnly.get(),//full
            localOnly + remoteOnly.get() + dataDiff//conflict
        )
    }

    Column(
        FillMaxSizeModifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            uiState.full,
            onRefresh = ::load,
            render = { data ->
                val id = data.id
                CommentCard(
                    {
                        navToCommentEdit(id)
                    },
                    {
                        navToCommentCreate(id)
                    },
                    data,
                    uiState.conflict.any { it.id == id },
                    { isDeleted ->
                        if (isDeleted)
                            viewModel.remove(id)
                        else
                            viewModel.resetConflict(uiState.conflict.copyUnless { it.id == id })
                    },
                    showSnackBar
                )
            }
        )
    }
}
