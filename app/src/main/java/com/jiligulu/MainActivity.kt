package com.jiligulu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jiligulu.ui.theme.JiliguluTheme
import java.util.*
import unilang.alias.*
import component.ui.*
import component.data.*
import kotlin.text.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val postDataList = List(8) {
                PostData(
                    12384,
                    "Hello world!",
                    "The quick brown fox jumps over the lazy dog",
                    Date(),
                    Date()
                )
            }
            val commentDataList = List(8) {
                CommentData(
                    12384,
                    "The quick brown fox jumps over the lazy dog",
                    Date()
                )
            }

            AppTheme {
                App(
                    postDataList = postDataList,
                    commentDataList = commentDataList
                )
            }
        }
    }
}
