package component.editor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import data.db.LocalCommentDatabase
import data.grpc.CommentServiceSingleton
import kotlinx.coroutines.launch
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentEditor(
    bodyFocusRequester: FocusRequester,
    id: Optional<i64>,
    bindingId: Optional<i64>,
    isReply: Optional<Boolean>,
    afterSave: (String) -> Unit
) {
    val ctx = LocalContext.current
    val localCommentDao = LocalCommentDatabase.getDatabase(ctx).localCommentDao()
    val old = localCommentDao.maybe(id.get())

    var bodyText by rememberMutStateOf(
        if (id.isPresent) old!!.body
        else ""
    )

    val scope = rememberCoroutineScope()

    fun update() = scope.launch {
        localCommentDao.update(
            old!!.copy(
                body = bodyText,
                modifyTime = Date()
            )
        )
    }

    fun create() = scope.launch {
        val commentService = CommentServiceSingleton.getService(ctx).get()
        val created = commentService.create(bodyText, bindingId.get(), isReply.get()).get()

        localCommentDao.insert(created)
    }

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
            Button(
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    if (id.isPresent)
                        update()
                    else
                        create()

                    afterSave(bodyText)
                }
            ) {
                Text("Save")
            }
        }

        BasicTextField(
            modifier = FillMaxWidthModifier
                .padding(top = 10.dp)
                .focusRequester(bodyFocusRequester),
            value = bodyText,
            textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
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
}
