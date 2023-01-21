package component.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth()
    ) {
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            text = "gRPC channel"
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Host",
            "E.g., https://for.example.domain",
            false,
        ) {

        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Port",
            "E.g., 40040",
            false
        ) {

        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            text = "pilipala account"
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "User id",
            "E.g., 1001",
            false
        ) {

        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Password",
            "E.g., 114514",
            true
        ) {

        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    Settings(PaddingValues(0.dp))
}
