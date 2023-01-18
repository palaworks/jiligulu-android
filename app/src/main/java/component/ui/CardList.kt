package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.data.PostData
import java.util.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.Dp


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun <T> CardList(
    dataList: List<T>,
    dataRender: @Composable (T) -> Unit,
) {
    dataList.forEach {
        dataRender(it)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun CardListPreview() {
    CardList(List(4) {
        PostData(
            12384, "Hola", "Just hello world!", Date(), Date()
        )
    }) {
        PostCard({}, {}, it)
    }
}
