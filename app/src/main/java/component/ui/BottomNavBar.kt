package component.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.data.BottomNavBarItemData

@Composable
fun BottomNavBar(
    dataList: List<BottomNavBarItemData>,
    navTo: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
    ) {
        dataList.forEach {
            NavigationBarItem(
                selected = false,
                onClick = { navTo(it.route) },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.route
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    val dataList = listOf(
        BottomNavBarItemData(
            "_",
            Icons.Default.Article
        ),
        BottomNavBarItemData(
            "_",
            Icons.Default.Comment
        ),
        BottomNavBarItemData(
            "_",
            Icons.Default.Settings
        )
    )
    BottomNavBar(dataList) {}
}
