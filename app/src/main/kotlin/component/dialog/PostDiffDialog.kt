package component.dialog

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import component.card.PostDiffCard
import data.db.LocalPostDatabase
import data.grpc.PostServiceSingleton
import data.ui.PostData
import kotlinx.coroutines.launch
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

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

    var remotePost by rememberMutStateOf(Optional.empty<PostData>())

    val ctx = LocalContext.current

    var loaded by rememberMutStateOf(false)

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
                    {
                        afterApplyLocal()
                        onDismissRequest()
                    },
                    {
                        afterApplyRemote()
                        onDismissRequest()
                    }
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {},
        )
}
