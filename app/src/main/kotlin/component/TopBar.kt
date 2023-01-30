package component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit
) {
    val colors =
        TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )

    TopAppBar(
        title = { Text(title) },
        colors = colors,
        actions = actions
    )
}
