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
import ui.FillMaxSizeModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostScreen(
    contentPadding: PaddingValues,
    navToPostEditor: (i64) -> Unit,
    navToCreateComment: (i64) -> Unit,
) {
    val localPostDao = LocalPostDatabase.getDatabase(LocalContext.current).localPostDao()

    var fullPostList by rememberMutStateOf(listOf<PostData>())
    var conflictPostList by rememberMutStateOf(listOf<PostData>())

    val ctx = LocalContext.current
    val load = suspend {
        val postService = PostServiceSingleton.getService(ctx).get()

        val remoteIdSha256Map = postService.getAllSha256()
        val localPostList = localPostDao.getAll()

        val resolved = mutableListOf<PostData>()

        val conflict = localPostList
            .fold(mutableListOf<PostData>()) { acc, localPost ->
                acc.apply {
                    val localSha256 = PostData(localPost).sha256()
                    val remoteSha256 = remoteIdSha256Map[localPost.id]
                    if (remoteSha256 == null)
                        acc.add(PostData(localPost))//local only post
                    else {
                        remoteIdSha256Map.remove(localPost.id)
                        if (remoteSha256 == localSha256)
                            resolved.add(PostData(localPost))//resolved
                        else
                            acc.add(PostData(localPost))//conflict
                    }
                }
            } + remoteIdSha256Map.keys
            .map {
                postService.getOne(it).get()//add remote only post
            }

        conflictPostList = conflict
        fullPostList = conflict + resolved
    }

    Column(
        FillMaxSizeModifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            itemFetcher = {
                load()
                fullPostList
            },
            itemRender = { data ->
                val id = data.id
                PostCard(
                    { navToPostEditor(id) },
                    { navToCreateComment(id) },
                    data,
                    conflictPostList.any { it.id == id },
                    {},
                    {},
                )
            }
        )
    }
}

@Preview
@Composable
fun PostScreenPreview() {
}
