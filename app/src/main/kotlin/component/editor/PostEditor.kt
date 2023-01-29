package component.editor

import android.annotation.SuppressLint
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
import data.db.LocalPostDatabase
import data.grpc.PostServiceSingleton
import data.ui.PostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import unilang.type.none
import unilang.type.some
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostEditor(
    bodyFocusRequester: FocusRequester,
    id: Optional<i64>,
    navBack: () -> Unit
) {
    val ctx = LocalContext.current
    var initialized by rememberMutStateOf(false)

    var titleText by rememberMutStateOf("")
    var bodyText by rememberMutStateOf("")

    val coroutineScope = rememberCoroutineScope()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val dao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        val data = dao.getOne(id.get())
        titleText = data.title
        bodyText = data.body
        initialized = true
    }

    if (id.isPresent && !initialized)
        coroutineScope.launch { initialize() }

    suspend fun update() = withContext(Dispatchers.IO) {
        val dao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        val data = dao.getOne(id.get())
        dao.update(
            data.copy(
                title = titleText,
                body = bodyText,
                modifyTime = Date()
            )
        )
    }

    suspend fun create() = withContext(Dispatchers.IO) {
        val dao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        val service = PostServiceSingleton.getService(ctx).get()

        val data = service.create(titleText, bodyText).get()
        dao.insert(data)
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
                    coroutineScope.launch {
                        if (id.isPresent)
                            update()
                        else
                            create()
                        navBack()
                    }
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
