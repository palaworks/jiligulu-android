package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.data.PostData
import java.util.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun <T> CardList(
    itemFetcher: () -> List<T>,
    itemRender: @Composable (T) -> Unit,
) {
    var itemList by remember { mutableStateOf(itemFetcher()) }

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        itemList = itemFetcher()
        delay(500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            itemList.forEach {
                itemRender(it)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Preview
@Composable
fun CardListPreview() {
    CardList(
        itemFetcher = {
            List(4) {
                PostData(
                    12384, "Hola", "Just hello world!", Date(), Date()
                )
            }
        },
        itemRender = @Composable {
            PostCard({}, {}, it)
        }
    )
}
