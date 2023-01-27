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
import component.card.CommentCard
import data.db.LocalCommentDatabase
import data.grpc.CommentServiceSingleton
import data.ui.CommentData
import data.ui.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.FillMaxSizeModifier
import ui.state.CommentScreenViewModel
import unilang.alias.i64
import unilang.type.copyUnless
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentListScreen(
    contentPadding: PaddingValues,
    viewModel: CommentScreenViewModel,
    navToCommentEditor: (i64) -> Unit,
    navToCreateComment: (i64) -> Unit,
) {
    val ctx = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    suspend fun load() = withContext(Dispatchers.IO) {
        val localCommentDao = LocalCommentDatabase.getDatabase(ctx).localCommentDao()
        val commentService = CommentServiceSingleton.getService(ctx).get()

        val remoteIdSha256Map = commentService.getAllSha256()
        val localCommentList = localCommentDao.getAll()

        val resolved = mutableListOf<CommentData>()

        val conflict = localCommentList
            .fold(mutableListOf<CommentData>()) { acc, localComment ->
                acc.apply {
                    val localSha256 = localComment.sha256()
                    val remoteSha256 = remoteIdSha256Map[localComment.id]

                    if (remoteSha256 == null)
                        acc.add(localComment)//local only comment
                    else {
                        remoteIdSha256Map.remove(localComment.id)
                        if (remoteSha256 == localSha256)
                            resolved.add(localComment)//resolved
                        else
                            acc.add(localComment)//conflict
                    }
                }
            } + remoteIdSha256Map.keys
            .map {
                commentService.getOne(it).get()//add remote only comment
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
                CommentCard(
                    {
                        navToCommentEditor(id)
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
fun CommentScreenPreview() {
}
