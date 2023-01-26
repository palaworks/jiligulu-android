package component.dialog

import android.annotation.SuppressLint
import java.util.*
import android.os.Build
import data.ui.CommentData
import component.card.CommentDiffCard
import androidx.compose.ui.unit.dp
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import data.db.LocalCommentDatabase
import data.grpc.CommentServiceSingleton
import kotlinx.coroutines.launch
import unilang.alias.i64

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentDiffDialog(
    id: i64,
    onDismissRequest: () -> Unit,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit
) {
    //TODO async fetch
    val localData =
        LocalCommentDatabase.getDatabase(LocalContext.current).localCommentDao().maybe(id)

    val localComment = if (localData == null)
        Optional.empty()
    else
        Optional.of(CommentData(localData))

    var remoteComment by remember { mutableStateOf(Optional.empty<CommentData>()) }
    val ctx = LocalContext.current

    var loaded by remember { mutableStateOf(false) }

    rememberCoroutineScope().launch {
        val commentService = CommentServiceSingleton.getService(ctx).get()
        remoteComment = commentService.getOne(id)
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
                CommentDiffCard(
                    localComment,
                    remoteComment,
                    afterApplyLocal,
                    afterApplyRemote
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {},
        )
}
