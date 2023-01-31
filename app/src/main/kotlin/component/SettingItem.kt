package component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import component.dialog.SettingDialog
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf

@Composable
fun SettingItem(
    onSave: (String) -> Unit,
    title: String,
    hint: String,
    passwordMode: Boolean,
    init: String,
) {
    var settingValue by rememberMutStateOf(init)
    var openDialog by rememberMutStateOf(false)

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

    Column(modifier = FillMaxWidthModifier
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
        {},
        "Password",
        "E.g., 114514",
        true,
        "foo"
    )
}
