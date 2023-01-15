package com.jiligulu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jiligulu.ui.theme.JiliguluTheme
import java.util.*
import unilang.alias.*
import component.ui.*
import component.data.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val data = listOf(
                PostData(
                    12384, "Hola", "Just hello world!", Date(), Date()
                ),
                PostData(
                    12384, "Hola", "Just hello world!", Date(), Date()
                ),
                PostData(
                    12384, "Hola", "Just hello world!", Date(), Date()
                )
            )
            ListContent(data)
        }
    }
}
