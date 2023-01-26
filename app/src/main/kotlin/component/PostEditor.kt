package component

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.i64
import java.util.*

@Composable
fun PostEditor(
    bodyFocusRequester: FocusRequester,
    id: Optional<i64>,
    afterSave: (String, String) -> Unit
) {
    var titleText by rememberMutStateOf("")
    var bodyText by rememberMutStateOf("")

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
                onClick = { afterSave(titleText, bodyText) }
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

@Preview
@Composable
fun PostEditorPreview() {
    PostEditor(FocusRequester(), Optional.of(114514)) { _, _ -> }
}
