package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.data.PostData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListContent(data: List<PostData>) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        data.forEach {
            PostCard(data = it)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
fun ListContentPreview() {
    ListContent(
        listOf(
            PostData(
                12384, "Hola", "Just hello world!", Date(), Date()
            ),
            PostData(
                12384, "Hola", "Just hello world!", Date(), Date()
            ),
            PostData(
                12384, "Hola", "Just hello world!", Date(), Date()
            )
        )
    )
}
