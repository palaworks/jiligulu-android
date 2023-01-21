package com.jiligulu

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import java.util.*
import unilang.alias.*
import component.ui.*
import component.data.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                AppScreen(
                    postDataList = postDataList,
                    commentDataList = commentDataList
                )
            }
        }
    }
}
