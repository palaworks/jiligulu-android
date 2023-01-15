package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            BottomNavBar()
        }
    ) { contentPadding ->
        CardList(contentPadding, postDataList) {
            PostCard(it)
        }
    }
}