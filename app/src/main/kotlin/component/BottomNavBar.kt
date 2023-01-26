package component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import data.ui.BottomNavBarItemData
import ui.FillMaxWidthModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    dataList: List<BottomNavBarItemData>,
    navTo: (String) -> Unit
) {
    val st by navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = FillMaxWidthModifier
            .height(72.dp)
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
