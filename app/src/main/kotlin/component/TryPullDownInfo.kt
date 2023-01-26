package component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ui.FillMaxWidthModifier

@Composable
fun TryPullDownInfo() {
    BoxWithConstraints {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(this@BoxWithConstraints.maxHeight),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.South,
                    contentDescription = "No local data",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = FillMaxWidthModifier
                        .size(100.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "No local data\nTry pull down to fetch data from remote",
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = FillMaxWidthModifier
                )
            }
        }
    }
}