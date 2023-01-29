package component.card

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.dialog.CommentDiffDialog
import data.db.LocalCommentDatabase
import data.ui.CommentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.*
import unilang.time.format
import unilang.time.yyMdHmm
import unilang.type.notNullThen
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Composable
fun CommentCard(
    navToEdit: () -> Unit,
    navToCreateComment: () -> Unit,
    data: CommentData,
    existDiff: Boolean,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit,
) {
    var showDiffDialog by rememberMutStateOf(false)

    if (showDiffDialog)
        CommentDiffDialog(
            data.id,
            { showDiffDialog = false },
            afterApplyLocal,
            afterApplyRemote
        )

    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    suspend fun ifExistLocalThenEdit() = withContext(Dispatchers.IO) {
        val dao = LocalCommentDatabase.getDatabase(ctx).localCommentDao()
        dao.maybe(data.id).notNullThen { navToEdit() }
    }

    Card(
        modifier = Modifier
            .clickable { coroutineScope.launch { ifExistLocalThenEdit() } },
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Row(
                modifier = FillMaxWidthModifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (existDiff)
                    IconButton(
                        onClick = { showDiffDialog = true },
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

                Spacer(Modifier.width(16.dp))
                Text(
                    text = data.body,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    modifier = FillMaxWidthModifier
                        .padding(bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = FillMaxWidthModifier, horizontalArrangement = Arrangement.SpaceBetween
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
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = data.createTime.format(yyMdHmm),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun CommentCardPreview() {
    Column {
        CommentCard(
            {}, {},
            CommentData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                114514,
                false,
                Date(),
                Date()
            ),
            true,
            {}, {},
        )
        CommentCard(
            {}, {},
            CommentData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                114514,
                true,
                Date(),
                Date()
            ),
            false,
            {}, {},
        )
    }
}