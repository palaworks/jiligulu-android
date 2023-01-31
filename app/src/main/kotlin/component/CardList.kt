package component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.FillMaxSizeModifier
import ui.rememberMutStateOf
import unilang.type.some
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun <T> CardList(
    data: List<T>,
    doRefresh: suspend () -> Unit,
    render: @Composable (T) -> Unit,
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by rememberMutStateOf(false)

    fun refresh() = refreshScope.launch {
        refreshing = true
        doRefresh()
        delay(500)
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    if (data.isEmpty() && !refreshing)
        refresh()

    Box(FillMaxSizeModifier.pullRefresh(refreshState)) {
        if (data.isEmpty())
            TryPullDownInfo()
        else
            LazyColumn {
                item {
                    Spacer(Modifier.height(10.dp))
                }
                data.forEach {
                    item {
                        render(it)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}
