package component.screen

import android.annotation.SuppressLint
import java.util.*
import android.os.Build
import data.ui.CommentData
import component.CommentDiffCard
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
import data.db.LocalCommentDatabase
import data.grpc.CommentService
import kotlinx.coroutines.launch
import unilang.alias.i64

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentDiffScreen(
    contentPadding: PaddingValues,
    commentService: CommentService,
    id: i64
) {
    //TODO async fetch
    val localData =
        LocalCommentDatabase.getDatabase(LocalContext.current).localCommentDao().maybe(id)

    val localComment = if (localData == null)
        Optional.empty()
    else
        Optional.of(CommentData(localData))

    var remoteComment by remember { mutableStateOf(Optional.empty<CommentData>()) }
    rememberCoroutineScope().launch {
        remoteComment = commentService.getOne(id)
    }

    Column(
        modifier = androidx.compose.ui.Modifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CommentDiffCard(
            localComment,
            remoteComment,
            commentService,
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
    }
}

@Preview
@Composable
fun CommentDiffScreenPreview() {
}
