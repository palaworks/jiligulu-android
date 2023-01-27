package component.dialog

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import component.card.CommentDiffCard
import data.db.LocalCommentDatabase
import data.grpc.CommentServiceSingleton
import data.ui.CommentData
import kotlinx.coroutines.launch
import ui.rememberMutStateOf
import unilang.alias.i64
import unilang.type.none
import unilang.type.optional
import unilang.type.some
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentDiffDialog(
    id: i64,
    onDismissRequest: () -> Unit,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit
) {
    var localComment by rememberMutStateOf(none<CommentData>())
    var remoteComment by rememberMutStateOf(none<CommentData>())

    val ctx = LocalContext.current

    var loaded by rememberMutStateOf(false)

    rememberCoroutineScope().launch {
        val dao = LocalCommentDatabase.getDatabase(ctx).localCommentDao()
        val service = CommentServiceSingleton.getService(ctx).get()

        localComment = dao.maybe(id).optional()
        remoteComment = service.getOne(id)

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
