package component.screen

import java.util.*
import android.os.Build
import unilang.alias.i64
import data.ui.CommentData
import androidx.compose.ui.unit.dp
import component.CommentDiffCard
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentDiffScreen(
    contentPadding: PaddingValues,
    id: i64
) {
    val localComment = CommentData(
        id, """Local Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(), Date()
    )
    val remoteComment = CommentData(
        id, """Remote Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(), Date()
    )
    Column(
        modifier = androidx.compose.ui.Modifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CommentDiffCard(Optional.of(localComment), Optional.of(remoteComment))
    }
}

@Preview
@Composable
fun CommentDiffScreenPreview() {
}
