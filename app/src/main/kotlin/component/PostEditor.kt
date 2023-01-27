package component

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
import data.db.LocalPost
import data.db.LocalPostDatabase
import data.grpc.PostServiceSingleton
import kotlinx.coroutines.launch
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostEditor(
    bodyFocusRequester: FocusRequester,
    id: Optional<i64>,
    afterSave: (String, String) -> Unit
) {
    val ctx = LocalContext.current
    val localPostDao = LocalPostDatabase.getDatabase(ctx).localPostDao()
    val old = localPostDao.maybe(id.get())

    var titleText by rememberMutStateOf(
        if (id.isPresent) old!!.title
        else ""
    )
    var bodyText by rememberMutStateOf(
        if (id.isPresent) old!!.body
        else ""
    )

    val scope = rememberCoroutineScope()

    fun update() = scope.launch {
        localPostDao.update(
            old!!.copy(
                title = titleText,
                body = bodyText,
                modifyTime = Date()
            )
        )
    }

    fun create() = scope.launch {
        val commentService = PostServiceSingleton.getService(ctx).get()
        val created = commentService.create(titleText, bodyText).get()

        localPostDao.insert(
            LocalPost(
                created.id,
                created.title,
                created.body,
                created.createTime,
                created.modifyTime,
            )
        )
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
                    contentDescription = "Post id",
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

                    afterSave(titleText, bodyText)
                }
            ) {
                Text("Save")
            }
        }

        BasicTextField(
            modifier = FillMaxWidthModifier
                .padding(vertical = 10.dp),
            value = titleText,
            textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            decorationBox = { innerTextField ->
                if (titleText.isEmpty())
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                innerTextField()
            },
            onValueChange = { titleText = it }
        )

        BasicTextField(
            modifier = FillMaxWidthModifier
                .focusRequester(bodyFocusRequester),
            value = bodyText,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            decorationBox = { innerTextField ->
                if (bodyText.isEmpty())
                    Text(
                        text = "Body",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                innerTextField()
            },
            onValueChange = { bodyText = it }
        )
    }
}
