package component.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingInputBox(
    label: String,
    hint: String,
) {
    Card {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 20.dp)
            )

            TextField(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                value = "",
                placeholder = { Text(hint) },
                onValueChange = {}
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingInputBoxPreview() {
    SettingInputBox(
        "Hello world!",
        "The quick brown fox jumps over the lazy dog."
    )
}
