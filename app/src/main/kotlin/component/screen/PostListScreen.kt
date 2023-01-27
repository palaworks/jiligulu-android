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
import data.db.LocalPostDatabase
import data.grpc.PostServiceSingleton
import data.ui.PostData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.state.PostScreenViewModel
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostListScreen(
    contentPadding: PaddingValues,
    viewModel: PostScreenViewModel,
    navToPostEditor: (i64) -> Unit,
    navToCreateComment: (i64) -> Unit,
) {
    val ctx = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    suspend fun load() = withContext(Dispatchers.IO) {
        val localPostDao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        val postService = PostServiceSingleton.getService(ctx).get()

        val remoteIdSha256Map = postService.getAllSha256()
        val localPostList = localPostDao.getAll()

        val resolved = mutableListOf<PostData>()

        val conflict = localPostList
            .fold(mutableListOf<PostData>()) { acc, localPost ->
                acc.apply {
                    val localSha256 = localPost.sha256()
                    val remoteSha256 = remoteIdSha256Map[localPost.id]
                    if (remoteSha256 == null)
                        acc.add(localPost)//local only post
                    else {
                        remoteIdSha256Map.remove(localPost.id)
                        if (remoteSha256 == localSha256)
                            resolved.add(localPost)//resolved
                        else
                            acc.add(localPost)//conflict
                    }
                }
            } + remoteIdSha256Map.keys
            .map {
                postService.getOne(it).get()//add remote only post
            }

        viewModel.reset(conflict + resolved, conflict)
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
                        navToPostEditor(id)
                    },
                    {
                        navToCreateComment(id)
                    },
                    data,
                    uiState.conflict.any { it.id == id },
                    {
                        viewModel.resetConflict(uiState.conflict.copyUnless { it.id == id })
                    },
                    {
                        viewModel.resetConflict(uiState.conflict.copyUnless { it.id == id })
                    },
                )
            }
        )
    }
}

@Preview
@Composable
fun PostScreenPreview() {
}
