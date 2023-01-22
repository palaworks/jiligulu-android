package component.screen

import java.util.*
import data.ui.PostData
import unilang.alias.i64
import component.CardList
import component.PostCard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues

val postDataList = List(8) {
    PostData(
        12384,
        "Hello world!",
        "The quick brown fox jumps over the lazy dog",
        Date(),
        Date()
    )
}

@Composable
fun PostScreen(
    contentPadding: PaddingValues,
    navToPostDiff: (i64) -> Unit,
    navToPostEditor: (i64) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            itemFetcher = { postDataList },
            itemRender = {
                PostCard(navToPostDiff, navToPostEditor, it)
            }
        )
    }
}

@Preview
@Composable
fun PostScreenPreview() {
}
