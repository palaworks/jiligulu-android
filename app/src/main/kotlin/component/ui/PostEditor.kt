package component.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import unilang.alias.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEditor(
    bodyFocusRequester: FocusRequester,
    id: i64,
    afterSave: (String, String) -> Unit
) {
    var titleText by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Numbers,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    contentDescription = "Post id",
                )
                Text(
                    text = id.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            Button(
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = { afterSave(titleText, bodyText) }
            ) {
                Text("Save")
            }
        }

        BasicTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
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
            modifier = Modifier
                .fillMaxWidth()
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

@Preview
@Composable
fun PostEditorPreview() {
    PostEditor(FocusRequester(), 114514) { _, _ -> }
}
