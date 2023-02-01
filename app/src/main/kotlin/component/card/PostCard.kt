package component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import component.dialog.DeleteConfirmDialog
import component.dialog.PostDiffDialog
import data.db.LocalPostDbSingleton
import data.ui.PostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.foxDog
import ui.foxDogX3
import ui.rememberMutStateOf
import unilang.alias.*
import unilang.time.format
import unilang.time.yyMdHmm
import java.util.*

@OptIn(ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PostCard(
    navToEditor: () -> Unit,
    navToCommentCreate: () -> Unit,
    afterConflictResolved: (isDeleted: Boolean) -> Unit,
    showSnackBar: (String) -> Unit,
    doDelete: () -> Unit,
    data: PostData,
    existDiff: Boolean,
) {
    var showDiffDialog by rememberMutStateOf(false)
    if (showDiffDialog)
        PostDiffDialog(
            data.id,
            { showDiffDialog = false },
            afterConflictResolved,
            afterConflictResolved,
            showSnackBar
        )

    var showDeleteDialog by rememberMutStateOf(false)
    if (showDeleteDialog)
        DeleteConfirmDialog(
            title = "Delete post #${data.id}",
            onConfirm = doDelete,
            onDismiss = { showDeleteDialog = false }
        )

    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun tryDoEdit() = coroutineScope.launch {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        if (dao.maybe(data.id) != null)
            navToEditor()
        else
            showSnackBar("No local data: please resolve conflict first.")
    }
    fun tryDoDelete() = coroutineScope.launch {
        val dao = LocalPostDbSingleton(ctx).localPostDao()
        if (dao.maybe(data.id) != null)
            showDeleteDialog = true
        else
            showSnackBar("No local data: please resolve conflict first.")
    }

    val isNote = data.title.isEmpty()

    val titlePart = @Composable {
        Text(
            text = data.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    val bodyPart = @Composable { maxLines: i32 ->
        Text(
            text = data.body.replace("\n", ""),
            style = MaterialTheme.typography.bodyMedium.copy(
                lineBreak = LineBreak.Simple
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 10.dp),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
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
            FillMaxWidthModifier.padding(20.dp)
        ) {
            Column(
                FillMaxWidthModifier
            ) {
                Row(
                    modifier = FillMaxWidthModifier,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (existDiff) {
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
                    } else IconButton(
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
                    Column(FillMaxWidthModifier) {
                        if (!isNote) {
                            titlePart()
                            Spacer(Modifier.height(6.dp))
                        }
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
                        if (isNote)
                            bodyPart(3)
                    }
                }
                if (!isNote)
                    bodyPart(2)
            }

            Row(
                modifier = FillMaxWidthModifier, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Create time",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = data.createTime.format(yyMdHmm),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
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
fun PostCardPreview() {
    Column {
        PostCard(
            {}, {}, {}, {}, {},
            PostData(
                12384, foxDog, foxDog, Date(), Date()
            ),
            true,
        )
        PostCard(
            {}, {}, {}, {}, {},
            PostData(
                12384, foxDog, foxDogX3, Date(), Date()
            ),
            false,
        )
        PostCard(
            {}, {}, {}, {}, {},
            PostData(
                12384, "", foxDogX3, Date(), Date()
            ),
            false,
        )
    }
}