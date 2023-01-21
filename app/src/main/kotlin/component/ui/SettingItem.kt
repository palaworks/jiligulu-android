package component.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingItem(
    title: String,
    hint: String,
    passwordMode: Boolean,
    onSave: (String) -> Unit
) {
    var settingValue by remember { mutableStateOf("") }
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog)
        SettingDialog(
            title = title,
            hint = hint,
            initial = settingValue,
            passwordMode = passwordMode,
            onDismiss = { openDialog = false },
            onConfirm = {
                settingValue = it
                onSave(it)
            })

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            openDialog = true
        }
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = title
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,

            text = if (passwordMode && settingValue != "") "******" else settingValue
        )
    }
}

@Preview
@Composable
fun SettingItemPreview() {
}
