package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import component.data.BottomNavBarItemData
import component.data.CommentData
import component.data.PostData

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    postDataList: List<PostData>,
    commentDataList: List<CommentData>
) {
    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            CreateButton {}
        },
        bottomBar = {
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
            BottomNavBar(dataList)
        }
    ) { contentPadding ->
        CardList(contentPadding, postDataList) {
            PostCard(it)
        }
    }
}