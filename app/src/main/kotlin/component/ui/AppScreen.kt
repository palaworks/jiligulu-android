package component.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import component.data.BottomNavBarItemData
import component.data.CommentData
import component.data.PostData
import component.ui.screen.*
import route.AppRoute
import java.util.*
import unilang.alias.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppScreen(
) {
    val navController = rememberNavController()
    val st by navController.currentBackStackEntryAsState()

    var title by remember { mutableStateOf("Posts") }

    val idNavArg = navArgument("id") { type = NavType.LongType }
    val getIdNavArg = { entry: NavBackStackEntry ->
        entry.arguments!!.getLong("id")
    }

    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            st?.destination?.hierarchy?.any {
                when (it.route) {
                    AppRoute.POST_LIST -> {
                        CreateButton {
                            navController.navigate(AppRoute.CREATE_POST)
                        }
                        true
                    }
                    AppRoute.COMMENT_LIST -> {
                        CreateButton {
                            navController.navigate(AppRoute.CREATE_COMMENT)
                        }
                        true
                    }
                    else -> false
                }
            }
        },
        topBar = {
            TopBar(title)
        },
        bottomBar = {
            val dataList = listOf(
                BottomNavBarItemData(
                    AppRoute.POST_LIST, Icons.Outlined.Article, Icons.Default.Article
                ),
                BottomNavBarItemData(
                    AppRoute.COMMENT_LIST, Icons.Outlined.Comment, Icons.Default.Comment
                ),
                BottomNavBarItemData(
                    AppRoute.SETTINGS, Icons.Outlined.Settings, Icons.Default.Settings
                )
            )
            BottomNavBar(navController, dataList) {
                navController.navigate(it)
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController, startDestination = AppRoute.POST_LIST
        ) {
            composable(AppRoute.POST_LIST) {
                title = "Posts"

                PostScreen(
                    contentPadding = contentPadding,
                    navToPostDiff = { id: i64 ->
                        navController
                            .navigate("${AppRoute.POST_DIFF}/${id}")
                    },
                    navToPostEditor = { id: i64 ->
                        navController
                            .navigate("${AppRoute.MODIFY_POST}/${id}")
                    }
                )
            }
            composable(
                "${AppRoute.POST_DIFF}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Post conflict"

                val id = getIdNavArg(entry)
                PostDiffScreen(contentPadding = contentPadding, id = id)
            }
            composable(AppRoute.CREATE_POST) {
                title = "Create post"

                PostEditScreen(contentPadding = contentPadding, id = Optional.empty())
            }
            composable(
                "${AppRoute.MODIFY_POST}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Edit post"

                val id = getIdNavArg(entry)
                PostEditScreen(contentPadding = contentPadding, id = Optional.of(id))
            }

            composable(AppRoute.COMMENT_LIST) {
                title = "Comments"

                CommentScreen(
                    contentPadding = contentPadding,
                    navToCommentDiff = { id: i64 ->
                        navController
                            .navigate("${AppRoute.COMMENT_DIFF}/${id}")
                    },
                    navToCommentEditor = { id: i64 ->
                        navController
                            .navigate("${AppRoute.MODIFY_COMMENT}/${id}")
                    }
                )
            }
            composable(
                "${AppRoute.COMMENT_DIFF}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Comment conflict"

                val id = getIdNavArg(entry)
                CommentDiffScreen(contentPadding = contentPadding, id = id)
            }
            composable(AppRoute.CREATE_COMMENT) {
                title = "Create comment"

                CommentEditScreen(contentPadding = contentPadding, id = Optional.empty())
            }
            composable(
                "${AppRoute.MODIFY_COMMENT}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Edit comment"

                val id = getIdNavArg(entry)
                CommentEditScreen(contentPadding = contentPadding, id = Optional.of(id))
            }

            composable(AppRoute.SETTINGS) {
                title = "Settings"

                SettingsScreen(contentPadding = contentPadding)
            }
        }
    }
}