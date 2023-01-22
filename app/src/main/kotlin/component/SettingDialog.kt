package component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(
    title: String,
    hint: String,
    initial: String,
    passwordMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            TextField(modifier = Modifier.fillMaxWidth(),
                value = text,
                placeholder = { Text(hint) },
                visualTransformation = if (passwordMode)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                onValueChange = {
                    text = it
                })
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(text)
                onDismiss()
            }
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SettingDialogPreview() {
    SettingDialog(
        "Hello world!",
        "The quick brown fox jumps over the lazy dog.",
        "",
        false,
        {},
    ) {}
}
