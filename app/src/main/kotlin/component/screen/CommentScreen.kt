package component.screen

import android.annotation.SuppressLint
import java.util.*
import unilang.alias.i64
import component.CardList
import component.CommentCard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import data.db.LocalCommentDatabase
import data.grpc.CommentService
import data.ui.CommentData
import data.ui.sha256
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentScreen(
    contentPadding: PaddingValues,
    navToCommentDiff: (i64) -> Unit,
    navToCommentEditor: (i64) -> Unit,
    navToCreateComment: (i64) -> Unit,
    commentService: CommentService
) {
    val localCommentDao = LocalCommentDatabase.getDatabase(LocalContext.current).localCommentDao()

    var fullCommentList by remember { mutableStateOf(listOf<CommentData>()) }
    var conflictCommentList by remember { mutableStateOf(listOf<CommentData>()) }

    val load = suspend {
        val remoteIdSha256Map = commentService.getAllSha256()
        val localCommentList = localCommentDao.getAll()

        val resolved = mutableListOf<CommentData>()

        val conflict = localCommentList
            .fold(mutableListOf<CommentData>()) { acc, localComment ->
                acc.apply {
                    val localSha256 = CommentData(localComment).sha256()
                    val remoteSha256 = remoteIdSha256Map[localComment.id]
                    if (remoteSha256 == null)
                        acc.add(CommentData(localComment))//local only comment
                    else {
                        remoteIdSha256Map.remove(localComment.id)
                        if (remoteSha256 == localSha256)
                            resolved.add(CommentData(localComment))//resolved
                        else
                            acc.add(CommentData(localComment))//conflict
                    }
                }
            } + remoteIdSha256Map.keys
            .map {
                commentService.getOne(it).get()//add remote only comment
            }

        conflictCommentList = conflict
        fullCommentList = conflict + resolved
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            itemFetcher = {
                load()
                fullCommentList
            },
            itemRender = { data ->
                val id = data.id
                CommentCard(
                    if (conflictCommentList.any { it.id == id })
                        Optional.of { navToCommentDiff(id) }
                    else
                        Optional.empty(),
                    { navToCommentEditor(id) },
                    { navToCreateComment(id) },
                    data
                )
            }
        )
    }
}

@Preview
@Composable
fun CommentScreenPreview() {
}
