package component.dialog

import android.annotation.SuppressLint
import java.util.*
import android.os.Build
import data.ui.PostData
import component.card.PostDiffCard
import androidx.compose.ui.unit.dp
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import component.card.CommentDiffCard
import data.db.LocalPostDatabase
import data.grpc.PostServiceSingleton
import kotlinx.coroutines.launch
import unilang.alias.i64

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostDiffDialog(
    id: i64,
    onDismissRequest: () -> Unit,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit
) {
    //TODO async fetch
    val localData = LocalPostDatabase.getDatabase(LocalContext.current).localPostDao().maybe(id)

    val localPost = if (localData == null)
        Optional.empty()
    else
        Optional.of(PostData(localData))

    var remotePost by remember { mutableStateOf(Optional.empty<PostData>()) }

    val ctx = LocalContext.current

    var loaded by remember { mutableStateOf(false) }

    rememberCoroutineScope().launch {
        val postService = PostServiceSingleton.getService(ctx).get()
        remotePost = postService.getOne(id)
        loaded = true
    }

    if (loaded)
        AlertDialog(
            title = {
                Text(
                    text = "Resolve diff",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                PostDiffCard(
                    localPost,
                    remotePost,
                    afterApplyLocal,
                    afterApplyRemote
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {},
        )
}
