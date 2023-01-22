package component

import androidx.compose.ui.unit.dp
import data.ui.BottomNavBarItemData
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.outlined.Settings
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    dataList: List<BottomNavBarItemData>,
    navTo: (String) -> Unit
) {
    val st by navController.currentBackStackEntryAsState()
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
                    val selected =
                        st?.destination?.hierarchy?.any { x -> it.route == x.route } == true
                    BadgedBox(
                        badge = { Badge { Text("2") } }
                    ) {
                        Icon(
                            imageVector =
                            if (selected)
                                it.selectedIcon
                            else
                                it.icon,
                            contentDescription = it.route
                        )
                    }
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
            Icons.Outlined.Article,
            Icons.Default.Article,
        ),
        BottomNavBarItemData(
            "_",
            Icons.Outlined.Comment,
            Icons.Default.Comment,
        ),
        BottomNavBarItemData(
            "_",
            Icons.Outlined.Settings,
            Icons.Default.Settings,
        )
    )
    val rnc = rememberNavController()
    BottomNavBar(rnc, dataList) {}
}
