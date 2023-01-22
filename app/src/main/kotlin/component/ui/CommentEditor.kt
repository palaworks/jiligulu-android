package component.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import unilang.alias.i64
import java.util.Optional

@Composable
fun CommentEditor(
    bodyFocusRequester: FocusRequester,
    id: Optional<i64>,
    afterSave: (String) -> Unit
) {
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
                onClick = { afterSave(bodyText) }
            ) {
                Text("Save")
            }
        }

        BasicTextField(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
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

@Preview
@Composable
fun CommentEditorPreview() {
    CommentEditor(FocusRequester(), Optional.of(114514)) {}
}
