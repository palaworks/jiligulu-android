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
import unilang.type.none
import unilang.type.optional
import unilang.type.some
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
    var localPost by rememberMutStateOf(none<PostData>())
    var remotePost by rememberMutStateOf(none<PostData>())

    val ctx = LocalContext.current

    var loaded by rememberMutStateOf(false)

    rememberCoroutineScope().launch {
        val dao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        val service = PostServiceSingleton.getService(ctx).get()

        localPost = dao.maybe(id).optional()
        remotePost = service.getOne(id)

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
