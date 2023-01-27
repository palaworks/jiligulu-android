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
import component.editor.PostEditor
import ui.FillMaxSizeModifier
import unilang.alias.i64
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostEditScreen(
    contentPadding: PaddingValues,
    id: Optional<i64>
) {
    val fr = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.padding(bottom = 40.dp)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                PostEditor(fr, id) { _, _ -> }
            }

            NoRipple {
                Column {
                    Spacer(modifier = FillMaxSizeModifier
                        .clickable { fr.requestFocus() })
                }
            }
        }
    }
}

@Composable
fun PostEditScreenPreview() {
}
