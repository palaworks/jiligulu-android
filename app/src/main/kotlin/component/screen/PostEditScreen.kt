package component.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.ui.PostEditor
import unilang.alias.i64
import java.util.Optional

@Composable
fun PostEditScreen(
    contentPadding: PaddingValues,
    id: Optional<i64>
) {
    val fr = remember { FocusRequester() }

    Column(
        modifier = androidx.compose.ui.Modifier
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

            CompositionLocalProvider(
                LocalRippleTheme provides object : RippleTheme {
                    @Composable
                    override fun defaultColor(): Color = Color.Transparent

                    @Composable
                    override fun rippleAlpha() = RippleAlpha(
                        draggedAlpha = 0.0f,
                        focusedAlpha = 0.0f,
                        hoveredAlpha = 0.0f,
                        pressedAlpha = 0.0f,
                    )
                }) {
                Column {
                    Spacer(modifier = Modifier
                        .fillMaxSize()
                        .clickable { fr.requestFocus() })
                }
            }
        }
    }
}

@Composable
fun PostEditScreenPreview() {
}
