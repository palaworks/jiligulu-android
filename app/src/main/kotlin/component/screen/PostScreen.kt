package component.screen

import android.annotation.SuppressLint
import java.util.*
import data.ui.PostData
import unilang.alias.i64
import component.CardList
import component.PostCard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import data.db.LocalPostDatabase
import data.grpc.PostService
import data.ui.sha256
import kotlinx.coroutines.launch
import unilang.type.notNullThen
import unilang.type.nullThen

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostScreen(
    contentPadding: PaddingValues,
    navToPostDiff: (i64) -> Unit,
    navToPostEditor: (i64) -> Unit,
    postService: PostService,
) {
    val localPostDao = LocalPostDatabase.getDatabase(LocalContext.current).localPostDao()

    val load = suspend {
        val remoteIdSha256Map = postService.getAllSha256()
        val localPostList = localPostDao.getAll()

        val resolvedPostList = mutableListOf<PostData>()

        val conflictPostList = localPostList
            .fold(mutableListOf<PostData>()) { acc, localPost ->
                acc.apply {
                    val localSha256 = PostData(localPost).sha256()
                    val remoteSha256 = remoteIdSha256Map[localPost.id]
                    if (remoteSha256 == null)
                        acc.add(PostData(localPost))//local only post
                    else {
                        remoteIdSha256Map.remove(localPost.id)
                        if (remoteSha256 == localSha256)
                            resolvedPostList.add(PostData(localPost))//resolved
                        else
                            acc.add(PostData(localPost))//conflict
                    }
                }
            } + remoteIdSha256Map.keys
            .map {
                postService.getOne(it).get()//add remote only post
            }

        Pair(conflictPostList, resolvedPostList)
    }

    var fullPostList by remember { mutableStateOf(listOf<PostData>()) }
    var conflictPostList by remember { mutableStateOf(listOf<PostData>()) }

    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        val (conflict, resolved) = load()

        conflictPostList = conflict
        fullPostList = conflict + resolved
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            itemFetcher = { fullPostList },
            itemRender = {
                PostCard(
                    if (conflictPostList.any { x -> x.id == it.id })
                        Optional.of(navToPostDiff)
                    else
                        Optional.empty(),
                    navToPostEditor,
                    it,
                )
            }
        )
    }
}

@Preview
@Composable
fun PostScreenPreview() {
}
