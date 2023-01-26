package component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import component.screen.*
import global.AppRoute
import global.bottomNavBarItems
import ui.rememberMutStateOf
import unilang.alias.*
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val state by navController.currentBackStackEntryAsState()

    var title by rememberMutStateOf("Posts")

    val idNavArg = navArgument("id") { type = NavType.LongType }
    val getIdNavArg = { entry: NavBackStackEntry ->
        entry.arguments!!.getLong("id")
    }

    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            state?.destination?.hierarchy?.any {
                when (it.route) {
                    AppRoute.POST_LIST -> {
                        CreateButton {
                            navController.navigate(AppRoute.CREATE_POST)
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
            BottomNavBar(navController, bottomNavBarItems) {
                navController.navigate(it)
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            //startDestination = AppRoute.POST_LIST,
            startDestination = AppRoute.SETTINGS
        ) {
            composable(AppRoute.POST_LIST) {
                title = "Posts"

                PostScreen(
                    contentPadding = contentPadding,
                    navToPostEditor = { id: i64 ->
                        navController.navigate("${AppRoute.MODIFY_POST}/$id")
                    },
                    navToCreateComment = { id: i64 ->
                        navController.navigate("${AppRoute.CREATE_COMMENT}/$id")
                    }
                )
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
                    navToCommentEditor = { id: i64 ->
                        navController.navigate("${AppRoute.MODIFY_COMMENT}/$id")
                    },
                    navToCreateComment = { id: i64 ->
                        navController.navigate("${AppRoute.CREATE_COMMENT}/$id")
                    }
                )
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