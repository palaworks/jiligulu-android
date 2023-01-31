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
import data.grpc.PostServiceSingleton
import data.ui.PostData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.state.PostListScreenViewModel
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostListScreen(
    contentPadding: PaddingValues,
    viewModel: PostListScreenViewModel,
    navToPostEdit: (i64) -> Unit,
    navToCommentCreate: (i64) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    suspend fun load() = withContext(Dispatchers.IO) {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val service = PostServiceSingleton(ctx).get()

        val local = dao.getAll()

        val remoteIdSha256Map = service.getAllSha256()
        if (remoteIdSha256Map.isEmpty) {
            showSnackBar("Network error: failed to load remote data.")
            viewModel.reset(local, listOf())
            return@withContext
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
            onRefresh = {
                load()
            },
            render = { data ->
                val id = data.id
                PostCard(
                    {
                        navToPostEdit(id)
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

@Preview
@Composable
fun PostScreenPreview() {
}
