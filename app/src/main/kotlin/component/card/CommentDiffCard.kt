package component.card

import java.util.*
import android.os.Build
import data.ui.CommentData
import unilang.hash.sha256
import java.text.SimpleDateFormat
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import data.db.LocalComment
import data.db.LocalCommentDatabase
import data.grpc.CommentService
import data.grpc.CommentServiceSingleton
import data.grpc.PostService
import data.ui.sha256
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SimpleDateFormat")
@Composable
fun CommentDiffCard(
    localComment: Optional<CommentData>,
    remoteComment: Optional<CommentData>,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit,
) {
    val fmt = SimpleDateFormat("yy-M-d h:mm")
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = "Comment id"
            )
            Text(
                text = localComment
                    .or { remoteComment }
                    .map { it.id }
                    .orElseThrow()
                    .toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Local data"
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Remote data"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sha256",
                    style = MaterialTheme.typography.labelLarge,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localComment.map { it.sha256() }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sha256",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = remoteComment.map { it.sha256() }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Body",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localComment.map { it.body }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Body",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = remoteComment.map { it.body }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modify time",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localComment.map { fmt.format(it.createTime) }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modify time",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = remoteComment.map { fmt.format(it.createTime) }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val coroutineScope = rememberCoroutineScope()
            val ctx = LocalContext.current

            fun applyLocal() = coroutineScope.launch {
                val commentService = CommentServiceSingleton.getService(ctx).get()
                //TODO handle err
                if (remoteComment.isEmpty) {
                    val comment = localComment.get()
                    commentService.create(comment.body, comment.bindingId, comment.isReply)
                } else if (localComment.isEmpty)
                    commentService.delete(remoteComment.get().id)
                else {
                    val comment = localComment.get()
                    commentService.update(
                        comment.id,
                        comment.body
                    )
                }
            }

            fun applyRemote() = coroutineScope.launch {
                val localCommentDao = LocalCommentDatabase.getDatabase(ctx).localCommentDao()
                val commentService = CommentServiceSingleton.getService(ctx).get()
                //TODO handle err
                if (localComment.isEmpty) {
                    val comment = remoteComment.get()
                    localCommentDao.insert(
                        LocalComment(
                            comment.id,
                            comment.body,
                            comment.bindingId,
                            comment.isReply,
                            comment.createTime,
                        )
                    )
                } else if (remoteComment.isEmpty)
                    localCommentDao.delete(localComment.get().id)
                else {
                    val comment = remoteComment.get()
                    commentService.update(
                        comment.id,
                        comment.body
                    )
                }
            }
            Button(
                onClick = {
                    applyLocal()
                    afterApplyLocal()
                },
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                val (text, icon) =
                    if (localComment.isEmpty)
                        Pair(
                            "Delete Remote",
                            Icons.Rounded.Delete
                        )
                    else
                        Pair(
                            "Push Local",
                            Icons.Rounded.DownloadForOffline
                        )
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = icon,
                    contentDescription = text
                )
                Text(text = text, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    applyRemote()
                    afterApplyRemote()
                },
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                val (text, icon) =
                    if (remoteComment.isEmpty)
                        Pair(
                            "Delete Local",
                            Icons.Rounded.Delete
                        )
                    else
                        Pair(
                            "Fetch Remote",
                            Icons.Rounded.DownloadForOffline
                        )
                Text(text = text, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = icon,
                    contentDescription = text
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun CommentDiffCardPreview() {
    val localComment = CommentData(
        24051968,
        """Local Body
          |The quick brown fox jumps over the lazy dog.
        """.trimMargin(),
        114514,
        false,
        Date()
    )
    val remoteComment = CommentData(
        24051968,
        """Remote Body
          |The quick brown fox jumps over the lazy dog.
        """.trimMargin(),
        114514,
        true,
        Date()
    )
    Column {
        //CommentDiffCard(Optional.of(localComment), Optional.of(remoteComment))
        CommentDiffCard(
            Optional.of(localComment),
            Optional.empty(),
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
        CommentDiffCard(
            Optional.empty(),
            Optional.of(remoteComment),
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
    }
}