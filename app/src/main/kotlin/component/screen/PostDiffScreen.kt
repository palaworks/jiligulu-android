package component.screen

import android.annotation.SuppressLint
import java.util.*
import android.os.Build
import data.ui.PostData
import component.PostDiffCard
import androidx.compose.ui.unit.dp
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import data.db.LocalPostDatabase
import data.grpc.PostService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import unilang.alias.i64

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostDiffScreen(
    contentPadding: PaddingValues,
    postService: PostService,
    id: i64
) {
    //TODO async fetch
    val localData = LocalPostDatabase.getDatabase(LocalContext.current).localPostDao().maybe(id)

    val localPost = if (localData == null)
        Optional.empty()
    else
        Optional.of(PostData(localData))

    var remotePost by remember { mutableStateOf(Optional.empty<PostData>()) }

    rememberCoroutineScope().launch {
        remotePost = postService.getOne(id)
    }

    Column(
        modifier = androidx.compose.ui.Modifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PostDiffCard(
            localPost,
            remotePost,
            postService,
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
    }
}

@Preview
@Composable
fun PostDiffScreenPreview() {
}
