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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import component.dialog.PostDiffDialog
import data.db.LocalPostDatabase
import data.ui.PostData
import kotlinx.coroutines.launch
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.alias.*
import unilang.time.format
import unilang.time.yyMdHmm
import unilang.type.notNullThen
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SimpleDateFormat")
@Composable
fun PostCard(
    navToEdit: () -> Unit,
    navToCreateComment: () -> Unit,
    data: PostData,
    existDiff: Boolean,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit,
) {
    var showDiffDialog by rememberMutStateOf(false)

    if (showDiffDialog)
        PostDiffDialog(
            data.id,
            { showDiffDialog = false },
            afterApplyLocal,
            afterApplyRemote
        )

    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun ifExistLocalThenEdit() = coroutineScope.launch {
        val dao = LocalPostDatabase.getDatabase(ctx).localPostDao()
        dao.maybe(data.id).notNullThen { navToEdit() }
    }

    Card(
        modifier = Modifier
            .clickable { ifExistLocalThenEdit() },
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            FillMaxWidthModifier
                .padding(20.dp)
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
                    } else
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
                    Column(
                        modifier = FillMaxWidthModifier
                    )
                    {
                        Text(
                            text = data.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = "Post id",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = data.id.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                Text(
                    text = data.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 10.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = FillMaxWidthModifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.PostAdd,
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
                        imageVector = Icons.Default.EditNote,
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
            {}, {},
            PostData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                "The quick brown fox jumps over the lazy dog",
                Date(),
                Date()
            ),
            true,
            {}, {},
        )
        PostCard(
            {}, {},
            PostData(
                12384,
                "The quick brown fox jumps over the lazy dog",
                "The quick brown fox jumps over the lazy dog",
                Date(),
                Date()
            ),
            false,
            {}, {},
        )
    }
}