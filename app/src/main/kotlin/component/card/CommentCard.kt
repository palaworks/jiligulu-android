package component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.dialog.CommentDiffDialog
import component.dialog.DeleteConfirmDialog
import data.db.LocalCommentDbSingleton
import data.ui.CommentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.*
import unilang.alias.*
import unilang.time.format
import unilang.time.yyMdHmm
import java.util.*

@OptIn(ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CommentCard(
    navToEditor: () -> Unit,
    navToCommentCreate: () -> Unit,
    afterConflictResolved: (isDeleted: Boolean) -> Unit,
    showSnackBar: (String) -> Unit,
    doDelete: () -> Unit,
    data: CommentData,
    existDiff: Boolean,
) {
    var showDiffDialog by rememberMutStateOf(false)
    if (showDiffDialog)
        CommentDiffDialog(
            data.id,
            { showDiffDialog = false },
            afterConflictResolved,
            afterConflictResolved,
            showSnackBar
        )

    var showDeleteDialog by rememberMutStateOf(false)
    if (showDeleteDialog)
        DeleteConfirmDialog(
            title = "Delete comment #${data.id}",
            onConfirm = doDelete,
            onDismiss = { showDeleteDialog = false }
        )

    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun tryDoEdit() = coroutineScope.launch {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        if (dao.maybe(data.id) != null)
            navToEditor()
        else
            showSnackBar("No local data: please resolve conflict first.")
    }

    fun tryDoDelete() = coroutineScope.launch {
        val dao = LocalCommentDbSingleton(ctx).localCommentDao()
        if (dao.maybe(data.id) != null)
            showDeleteDialog = true
        else
            showSnackBar("No local data: please resolve conflict first.")
    }

    Card(
        modifier = Modifier.combinedClickable(
            onClick = { tryDoEdit() },
            onLongClick = { tryDoDelete() }
        ),
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
                        onClick = { navToCommentCreate() },
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

                Column {
                    Row(
                        FillMaxWidthModifier,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.Numbers,
                            contentDescription = "Comment id",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = data.id.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Row(
                        FillMaxWidthModifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Is reply",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (data.isReply) "Comment" else "Post",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = "Binding id",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = data.bindingId.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = data.body.replace("\n", ""),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineBreak = LineBreak.Simple
                        ),
                        maxLines = 3,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        modifier = FillMaxWidthModifier
                            .padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = FillMaxWidthModifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = "Create time",
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
                Row {
                    Icon(
                        imageVector = Icons.Default.DriveFileRenameOutline,
                        contentDescription = "Modify time",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = data.modifyTime.format(yyMdHmm),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
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
            {}, {}, {}, {}, {},
            CommentData(
                testId,
                foxDog,
                testId,
                false,
                Date(),
                Date()
            ),
            true,
        )
        CommentCard(
            {}, {}, {}, {}, {},
            CommentData(
                testId,
                foxDogX3,
                testId,
                true,
                Date(),
                Date()
            ),
            false,
        )
    }
}