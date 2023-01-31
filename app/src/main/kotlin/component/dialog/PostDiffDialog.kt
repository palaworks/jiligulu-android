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
import data.db.LocalPostDbSingleton
import data.grpc.PostServiceSingleton
import data.ui.PostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.rememberMutStateOf
import unilang.alias.i64
import unilang.type.none
import unilang.type.optional
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostDiffDialog(
    id: i64,
    onDismissRequest: () -> Unit,
    afterApplyLocal: (deleteRemote:Boolean) -> Unit,
    afterApplyRemote: (deleteLocal:Boolean) -> Unit,
    showSnackBar: (String) -> Unit
) {
    val ctx = LocalContext.current
    var initialized by rememberMutStateOf(false)

    var localData by rememberMutStateOf(none<PostData>())
    var remoteData by rememberMutStateOf(none<PostData>())

    val coroutineScope = rememberCoroutineScope()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val service = PostServiceSingleton(ctx).get()

        localData = dao.maybe(id).optional()
        remoteData = service.getOne(id)

        initialized = true
    }

    coroutineScope.launch { initialize() }

    if (initialized)

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
                    localData,
                    remoteData,
                    {
                        afterApplyLocal(localData.isEmpty)
                        onDismissRequest()
                    },
                    {
                        afterApplyRemote(remoteData.isEmpty)
                        onDismissRequest()
                    },
                    showSnackBar
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {},
        )
}
