package component.ui

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.clickable
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
    Spacer(modifier = Modifier.height(10.dp))
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
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Host"
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "https://for.example.domain"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Port"
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "40040"
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            text = "pilipala account"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "User id"
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "1001"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Password"
        )
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            text = "******"
        )

/*
        TextField(
            label = {
            },
            value = "https://for.example.domain",
            onValueChange = {}
        )
        TextField(
            label = {
            },
            value = "40040",
            onValueChange = {}
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            label = {
            },
            value = "1001",
            onValueChange = {}
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            label = {
            },
            value = "1234567890",
            onValueChange = {}
        )
*/
    }
}

@Preview
@Composable
fun SettingsPreview() {
    Settings(PaddingValues(0.dp))
}
