package component.screen

import java.util.*
import unilang.alias.i64
import data.ui.CommentData
import component.CardList
import component.CommentCard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues

val commentDataList = List(8) {
    CommentData(
        12384,
        "The quick brown fox jumps over the lazy dog",
        Date()
    )
}

@Composable
fun CommentScreen(
    contentPadding: PaddingValues,
    navToCommentDiff: (i64) -> Unit,
    navToCommentEditor: (i64) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
    ) {
        CardList(
            itemFetcher = { commentDataList },
            itemRender = {
                CommentCard(navToCommentDiff, navToCommentEditor, it)
            }
        )
    }
}

@Preview
@Composable
fun CommentScreenPreview() {
}
