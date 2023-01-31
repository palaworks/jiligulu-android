package component.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ui.foxDog

@Composable
fun DeleteConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = "This operation will only delete local data",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "CANCEL")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }
            ) {
                Text(
                    text = "delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Preview
@Composable
fun DeleteConfirmDialogPreview() {
    DeleteConfirmDialog(
        title = foxDog,
        onConfirm = {},
        onDismiss = {}
    )
}
