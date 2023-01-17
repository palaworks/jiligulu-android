package component.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import route.AppRoute
import java.util.*
import unilang.alias.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    postDataList: List<PostData>,
    commentDataList: List<CommentData>
) {
    val navController = rememberNavController()
    val st by navController.currentBackStackEntryAsState()
    val navToPostDiff = { id: i64 -> navController.navigate("${AppRoute.POST_DIFF}/${id}") }
    val navToCommentDiff = { id: i64 -> navController.navigate("${AppRoute.COMMENT_DIFF}/${id}") }
    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            val onList =
                st?.destination?.hierarchy?.any { x ->
                    AppRoute.POSTS == x.route
                            || AppRoute.COMMENTS == x.route
                } == true
            if (onList)
                CreateButton {}
        },
        bottomBar = {
            val dataList = listOf(
                BottomNavBarItemData(
                    AppRoute.POSTS,
                    Icons.Outlined.Article,
                    Icons.Default.Article
                ),
                BottomNavBarItemData(
                    AppRoute.COMMENTS,
                    Icons.Outlined.Comment,
                    Icons.Default.Comment
                ),
                BottomNavBarItemData(
                    AppRoute.SETTINGS,
                    Icons.Outlined.Settings,
                    Icons.Default.Settings
                )
            )
            BottomNavBar(navController, dataList) {
                navController.navigate(it)
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.POSTS
        ) {
            composable(AppRoute.POSTS) {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "Posts",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    CardList(postDataList) {
                        PostCard(navToPostDiff, it)
                    }
                }
            }
            composable(
                "${AppRoute.POST_DIFF}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments!!.getLong("id")
                val localPost = PostData(
                    id,
                    "Hello world!",
                    """Local Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
                    Date(),
                    Date(),
                )
                val remotePost = PostData(
                    id,
                    "Hello world!",
                    """Remote Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
                    Date(),
                    Date(),
                )
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "Post conflict",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    PostDiffCard(Optional.of(localPost), Optional.of(remotePost))
                }
            }
            composable(AppRoute.COMMENTS) {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "Comments",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    CardList(commentDataList) {
                        CommentCard(navToCommentDiff, it)
                    }
                }
            }
            composable(
                "${AppRoute.COMMENT_DIFF}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments!!.getLong("id")
                val localComment = CommentData(
                    id,
                    """Local Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
                    Date()
                )
                val remoteComment = CommentData(
                    id,
                    """Remote Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
                    Date()
                )
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "Comment conflict",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    CommentDiffCard(Optional.of(localComment), Optional.of(remoteComment))
                }
            }
            composable(AppRoute.SETTINGS) {
                Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        style = MaterialTheme.typography.headlineLarge,
                        text = "Settings"
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    Settings(contentPadding)
                }
            }
        }
    }
}