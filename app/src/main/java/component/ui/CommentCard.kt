package component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.node.modifierElementOf
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import unilang.alias.*
import component.data.*

@SuppressLint("SimpleDateFormat")
@Composable
fun CommentCard(
    navToDiff: (Long) -> Unit,
    navToEdit: (Long) -> Unit,
    data: CommentData,
    fullBody: Boolean = false
) {
    val fmt = SimpleDateFormat("yy-M-d h:mm")
    Card(
        modifier = Modifier
            .clickable { navToEdit(data.id) },
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navToDiff(data.id) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Merge,
                        contentDescription = "Resolve conflict",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = data.body,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = "Post id",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = data.id.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Row {
                    Icon(
                        imageVector = Icons.Outlined.AddComment,
                        contentDescription = "Create Time",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = fmt.format(data.createTime),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CommentCardPreview() {
    CommentCard(
        {},
        {},
        CommentData(
            12384,
            "The quick brown fox jumps over the lazy dog",
            Date()
        )
    )
}