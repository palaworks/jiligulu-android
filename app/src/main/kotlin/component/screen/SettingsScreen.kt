package component.screen

import component.Settings
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Settings(contentPadding)
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
}
