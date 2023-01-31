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
import data.db.LocalPostDbSingleton
import data.ui.PostData
import data.ui.PostEditMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import java.util.*

@OptIn(ExperimentalTextApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostEditor(
    mode: PostEditMode,
    navBack: () -> Unit
) {
    val ctx = LocalContext.current
    var initialized by rememberMutStateOf(false)

    var titleText by rememberMutStateOf("")
    var bodyText by rememberMutStateOf("")
    val fr = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val id = (mode as PostEditMode.Edit).id
        val data = dao.getOne(id)
        titleText = data.title
        bodyText = data.body
        initialized = true
    }

    if (mode is PostEditMode.Edit && !initialized)
        coroutineScope.launch { initialize() }

    suspend fun update() = withContext(Dispatchers.IO) {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val id = (mode as PostEditMode.Edit).id
        val data = dao
            .getOne(id)
            .copy(
                title = titleText,
                body = bodyText,
                modifyTime = Date()
            )
        dao.update(data)
    }

    suspend fun create() = withContext(Dispatchers.IO) {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        val minId = dao.getMinId()
        val id = if (minId > 0) -1 else minId - 1
        val data =
            PostData(
                id,
                titleText,
                bodyText,
                Date(),
                Date()
            )
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
                        contentDescription = "Post id",
                    )
                    val idText = when (mode) {
                        is PostEditMode.Edit -> mode.id.toString()
                        is PostEditMode.Create -> "New"
                    }
                    Text(
                        text = idText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                TextButton(
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        coroutineScope.launch {
                            when (mode) {
                                is PostEditMode.Edit -> update()
                                is PostEditMode.Create -> create()
                            }
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
                    .padding(vertical = 10.dp),
                value = titleText,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
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
                    .focusRequester(fr),
                value = bodyText,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
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
