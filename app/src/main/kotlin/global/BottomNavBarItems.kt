package global

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Settings
import data.ui.BottomNavBarItemData

val bottomNavBarItems = listOf(
    BottomNavBarItemData(
        AppRoute.POST_LIST,
        Icons.Outlined.Article,
        Icons.Default.Article
    ),
    BottomNavBarItemData(
        AppRoute.COMMENT_LIST,
        Icons.Outlined.Comment,
        Icons.Default.Comment
    ),
    BottomNavBarItemData(
        AppRoute.SETTINGS,
        Icons.Outlined.Settings,
        Icons.Default.Settings
    )
)
