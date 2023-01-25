package component

import java.util.*
import unilang.alias.*
import data.ui.CommentData
import java.text.SimpleDateFormat
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.AddComment

@SuppressLint("SimpleDateFormat")
@Composable
fun CommentCard(
    navToDiff: Optional<() -> Unit>,
    navToEdit: () -> Unit,
    navToCreateComment: () -> Unit,
    data: CommentData,
    fullBody: Boolean = false
) {
    val fmt = SimpleDateFormat("yy-M-d h:mm")
    Card(
        modifier = Modifier
            .clickable { navToEdit() },
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
                if (navToDiff.isPresent)
                    IconButton(
                        onClick = { navToDiff.get()() },
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
                else
                    IconButton(
                        onClick = { navToCreateComment() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Create comment",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = data.body,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = if (fullBody) i32.MAX_VALUE else 2,
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
    Column {
        CommentCard(
            Optional.of {},
            {}, {},
            CommentData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                114514,
                false,
                Date()
            )
        )
        CommentCard(
            Optional.empty(),
            {}, {},
            CommentData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                114514,
                true,
                Date()
            )
        )
    }
}