package component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.FillMaxSizeModifier
import ui.rememberMutStateOf
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun <T> CardList(
    data: List<T>,
    onRefresh: suspend () -> Unit,
    render: @Composable (T) -> Unit,
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by rememberMutStateOf(false)

    fun refresh() = refreshScope.launch {
        refreshing = true
        onRefresh()
        delay(500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    if (data.isEmpty())
        refresh()

    Box(FillMaxSizeModifier.pullRefresh(state)) {
        if (data.isEmpty())
            TryPullDownInfo()
        else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(10.dp))
                data.forEach {
                    render(it)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}
