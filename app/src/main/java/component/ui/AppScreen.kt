package component.ui

import android.annotation.SuppressLint
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import component.data.BottomNavBarItemData
import component.data.CommentData
import component.data.PostData
import route.AppRoute

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    postDataList: List<PostData>,
    commentDataList: List<CommentData>
) {
    val navController = rememberNavController()
    val st by navController.currentBackStackEntryAsState()
    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            val onSettings =
                st?.destination?.hierarchy?.any { x -> AppRoute.SETTINGS == x.route } == true
            if (!onSettings)
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
                        PostCard(it)
                    }
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
                        CommentCard(it)
                    }
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