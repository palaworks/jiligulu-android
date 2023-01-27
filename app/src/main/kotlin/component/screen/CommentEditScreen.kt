package component.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import component.NoRipple
import component.editor.CommentEditor
import ui.FillMaxSizeModifier
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentEditScreen(
    contentPadding: PaddingValues,
    id: Optional<i64>,
    bindingId: Optional<i64>,
    isReply: Optional<Boolean>,
) {
    val fr = remember { FocusRequester() }

    Column(Modifier.padding(contentPadding)) {
        Column(Modifier.padding(bottom = 40.dp)) {
            Column(
                Modifier
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                CommentEditor(
                    fr,
                    id,
                    bindingId,
                    isReply
                )
            }

            NoRipple {
                Column {
                    Spacer(FillMaxSizeModifier
                        .clickable { fr.requestFocus() })
                }
            }
        }
    }
}
