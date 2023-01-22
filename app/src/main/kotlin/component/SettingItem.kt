package component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SettingItem(
    title: String,
    hint: String,
    passwordMode: Boolean,
    init: String?,
    onSave: (String) -> Unit
) {
    var settingValue by remember { mutableStateOf(init.orEmpty()) }
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
    SettingItem(
        "Password",
        "E.g., 114514",
        true,
        "foo"
    ) {
    }
}
