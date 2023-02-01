package component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import component.screen.*
import data.ui.CommentEditMode
import data.ui.PostEditMode
import global.AppRoute
import global.bottomNavBarItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.rememberMutStateOf
import ui.state.CommentListScreenViewModel
import ui.state.CommentListScreenViewModelSingleton
import ui.state.PostListScreenViewModel
import ui.state.PostListScreenViewModelSingleton
import unilang.alias.*
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val state by navController.currentBackStackEntryAsState()

    @Composable
    fun onRoute(route: String, f: @Composable () -> Unit) {
        if (state
                ?.destination
                ?.hierarchy
                ?.any { it.route == route }
            == true
        ) f()
    }

    var title by rememberMutStateOf("")

    val idNavArg = navArgument("id") { type = NavType.LongType }
    val getIdNavArg = { entry: NavBackStackEntry ->
        entry.arguments!!.getLong("id")
    }

    fun navTo(dest: String) {
        navController.navigate(dest) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showSnackBar(message: String) =
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                message = message,
                actionLabel = "OK"
            )
        }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButton = {
            onRoute(AppRoute.POST_LIST) {
                CreateButton {
                    navTo(AppRoute.CREATE_POST)
                }
            }
        },
        topBar = {
            TopBar(title) {}
        },
        bottomBar = {
            BottomNavBar(
                navController,
                bottomNavBarItems,
                ::navTo
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            //startDestination = AppRoute.POST_LIST,
            startDestination = AppRoute.SETTINGS
        ) {
            composable(AppRoute.POST_LIST) {
                title = "Posts"

                PostListScreen(
                    contentPadding = contentPadding,
                    navToPostEdit = { id: i64 ->
                        navTo("${AppRoute.MODIFY_POST}/$id")
                    },
                    navToCommentCreate = { bindingId: i64 ->
                        navTo("${AppRoute.CREATE_COMMENT}/$bindingId-false")
                    },
                    showSnackBar = ::showSnackBar
                )
            }
            composable(AppRoute.CREATE_POST) {
                title = "Create post"

                PostEditScreen(
                    contentPadding = contentPadding,
                    mode = PostEditMode.Create,
                    afterCreated = {
                        navTo(AppRoute.POST_LIST)
                        PostListScreenViewModelSingleton().add(Pair(it, true))
                    },
                    afterUpdated = {},
                )
            }
            composable(
                "${AppRoute.MODIFY_POST}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Edit post"

                val id = getIdNavArg(entry)
                PostEditScreen(
                    contentPadding = contentPadding,
                    mode = PostEditMode.Edit(id),
                    afterCreated = {},
                    afterUpdated = {
                        navTo(AppRoute.POST_LIST)
                        //TODO detect conflict here
                    },
                )
            }

            composable(AppRoute.COMMENT_LIST) {
                title = "Comments"

                CommentListScreen(
                    contentPadding = contentPadding,
                    navToCommentEdit = { id: i64 ->
                        navTo("${AppRoute.MODIFY_COMMENT}/$id")
                    },
                    navToCommentCreate = { bindingId: i64 ->
                        navTo("${AppRoute.CREATE_COMMENT}/$bindingId-true")
                    },
                    showSnackBar = ::showSnackBar
                )
            }
            composable(
                "${AppRoute.CREATE_COMMENT}/{bindingId}-{isReply}",
                arguments = listOf(
                    navArgument("bindingId") { type = NavType.LongType },
                    navArgument("isReply") { type = NavType.BoolType }
                )
            ) { entry ->
                title = "Create comment"

                val bindingId = entry.arguments!!.getLong("bindingId")
                val isReply = entry.arguments!!.getBoolean("isReply")
                CommentEditScreen(
                    contentPadding = contentPadding,
                    mode = CommentEditMode.Create(bindingId, isReply),
                    afterCreated = {
                        navTo(AppRoute.COMMENT_LIST)
                        CommentListScreenViewModelSingleton().add(Pair(it, true))
                    },
                    afterUpdated = {},
                )
            }
            composable(
                "${AppRoute.MODIFY_COMMENT}/{id}",
                arguments = listOf(idNavArg)
            ) { entry ->
                title = "Edit comment"

                val id = getIdNavArg(entry)
                CommentEditScreen(
                    contentPadding = contentPadding,
                    mode = CommentEditMode.Edit(id),
                    afterCreated = {},
                    afterUpdated = {
                        navTo(AppRoute.COMMENT_LIST)
                        //TODO detect conflict here
                    },
                )
            }

            composable(AppRoute.SETTINGS) {
                title = "Settings"

                SettingsScreen(contentPadding = contentPadding)
            }
        }
    }
}