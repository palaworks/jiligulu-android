package component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import data.ui.BottomNavBarItemData
import global.AppRoute
import ui.FillMaxWidthModifier
import ui.state.CommentListScreenViewModel
import ui.state.CommentListScreenViewModelSingleton
import ui.state.PostListScreenViewModel
import ui.state.PostListScreenViewModelSingleton
import unilang.alias.i32

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    dataList: List<BottomNavBarItemData>,
    navTo: (String) -> Unit,
) {
    val postScreenUiState by PostListScreenViewModelSingleton().state.collectAsState()
    val commentScreenUiState by CommentListScreenViewModelSingleton().state.collectAsState()

    val st by navController.currentBackStackEntryAsState()

    val selected = { data: BottomNavBarItemData ->
        st?.destination?.hierarchy
            ?.any { data.route == it.route } == true
    }

    NavigationBar(FillMaxWidthModifier.height(72.dp)) {
        dataList.forEach {
            NavigationBarItem(
                selected = false,
                onClick = { navTo(it.route) },
                icon = {
                    @Composable
                    fun badge() = when (it.route) {
                        AppRoute.POST_LIST -> postScreenUiState.list.count { it.second }
                        AppRoute.COMMENT_LIST -> commentScreenUiState.list.count { it.second }
                        else -> 0
                    }.let { if (it != 0) Badge { Text(it.toString()) } }

                    BadgedBox({ badge() }) {
                        Icon(
                            imageVector =
                            if (selected(it))
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
