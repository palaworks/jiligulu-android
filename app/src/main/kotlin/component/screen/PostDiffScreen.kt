package component.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.ui.PostData
import component.ui.PostDiffCard
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostDiffScreen(
    contentPadding: PaddingValues,
    id: i64
) {
    val localPost = PostData(
        id,
        "Hello world!",
        """Local Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
        Date(),
        Date(),
    )
    val remotePost = PostData(
        id,
        "Hello world!",
        """Remote Body
                      |The quick brown fox jumps over the lazy dog.
                    """.trimMargin(),
        Date(),
        Date(),
    )

    Column(
        modifier = androidx.compose.ui.Modifier
            .padding(contentPadding)
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PostDiffCard(Optional.of(localPost), Optional.of(remotePost))
    }
}

@Preview
@Composable
fun PostDiffScreenPreview() {
}
