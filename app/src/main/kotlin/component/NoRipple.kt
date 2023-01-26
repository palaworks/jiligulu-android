package component

import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun NoRipple(content: @Composable () -> Unit) {
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
        }
    ) {
        content()
    }
}