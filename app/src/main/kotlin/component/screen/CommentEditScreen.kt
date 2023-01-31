package component.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.editor.CommentEditor
import data.ui.CommentData
import data.ui.CommentEditMode
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentEditScreen(
    contentPadding: PaddingValues,
    mode: CommentEditMode,
    afterCreated: (CommentData) -> Unit,
    afterUpdated: (CommentData) -> Unit,
) {
    Column(Modifier.padding(contentPadding)) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(10.dp))

            CommentEditor(
                mode,
                afterCreated,
                afterUpdated
            )
        }
    }
}
