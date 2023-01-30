package component.editor

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import component.NoRipple
import data.db.LocalCommentDbSingleton
import data.grpc.CommentServiceSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

@OptIn(ExperimentalTextApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentEditor(
    id: Optional<i64>,
    bindingId: Optional<i64>,
    isReply: Optional<Boolean>,
    navBack: () -> Unit
) {
    val ctx = LocalContext.current
    var initialized by rememberMutStateOf(false)

    var bodyText by rememberMutStateOf("")
    val fr = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        val data = dao.getOne(id.get())
        bodyText = data.body
        initialized = true
    }

    if (id.isPresent && !initialized)
        coroutineScope.launch { initialize() }

    suspend fun update() = withContext(Dispatchers.IO) {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        val data = dao.getOne(id.get())
        dao.update(
            data.copy(
                body = bodyText,
                modifyTime = Date()
            )
        )
    }

    suspend fun create() = withContext(Dispatchers.IO) {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        val service = CommentServiceSingleton(ctx).get()

        val data = service.create(bodyText, bindingId.get(), isReply.get()).get()
        dao.insert(data)
    }

    Column {
        Column {
            Row(
                modifier = FillMaxWidthModifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        contentDescription = "Comment id",
                    )
                    Text(
                        text = id.map { it.toString() }.orElse("New"),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                TextButton(
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        coroutineScope.launch {
                            if (id.isPresent)
                                update()
                            else
                                create()
                            navBack()
                        }
                    }
                ) {
                    Text(
                        text = "Save",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineBreak = LineBreak.Paragraph
                        )
                    )
                }
            }

            BasicTextField(
                modifier = FillMaxWidthModifier
                    .padding(top = 10.dp)
                    .focusRequester(fr),
                value = bodyText,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerTextField ->
                    if (bodyText.isEmpty())
                        Text(
                            text = "Body",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                    innerTextField()
                },
                onValueChange = { bodyText = it }
            )
        }

        NoRipple {
            Column {
                Spacer(
                    FillMaxWidthModifier
                        .height(50.dp)
                        .clickable {
                            fr.requestFocus()
                        }
                )
            }
        }
    }
}
