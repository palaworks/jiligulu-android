package component

import java.util.*
import data.ui.sha256
import data.ui.PostData
import android.os.Build
import java.text.SimpleDateFormat
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.annotation.RequiresApi
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import data.db.LocalPost
import data.db.LocalPostDatabase
import data.grpc.PostService
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Composable
fun PostDiffCard(
    localPost: Optional<PostData>,
    remotePost: Optional<PostData>,
    postService: PostService,
    afterApplyLocal: () -> Unit,
    afterApplyRemote: () -> Unit,
) {
    val fmt = SimpleDateFormat("yy-M-d h:mm")
    Column(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = "Comment id",
            )
            Text(
                text = localPost
                    .or { remotePost }
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
                    text = "Local post sha256",
                    style = MaterialTheme.typography.labelLarge,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localPost.map { it.sha256() }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "remote post sha256",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = remotePost.map { it.sha256() }.orElse("-"),
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
                    text = "Title",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localPost.map { it.title }.orElse("-"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = remotePost.map { it.title }.orElse("-"),
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
                    text = "Body",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = localPost.map { it.body }.orElse("-"),
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
                    text = remotePost.map { it.body }.orElse("-"),
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
                    text = localPost.map { fmt.format(it.createTime) }.orElse("-"),
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
                    text = remotePost.map { fmt.format(it.createTime) }.orElse("-"),
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
                //TODO handle err
                if (remotePost.isEmpty) {
                    val post = localPost.get()
                    postService.create(post.title, post.body)
                } else if (localPost.isEmpty)
                    postService.delete(remotePost.get().id)
                else {
                    val post = localPost.get()
                    postService.update(
                        post.id,
                        post.title,
                        post.body
                    )
                }
            }

            fun applyRemote() = coroutineScope.launch {
                val localPostDao = LocalPostDatabase.getDatabase(ctx).localPostDao()
                //TODO handle err
                if (localPost.isEmpty) {
                    val post = remotePost.get()
                    localPostDao.insert(
                        LocalPost(
                            post.id,
                            post.title,
                            post.body,
                            post.createTime,
                            post.modifyTime
                        )
                    )
                } else if (remotePost.isEmpty)
                    localPostDao.delete(localPost.get().id)
                else {
                    val post = remotePost.get()
                    postService.update(
                        post.id,
                        post.title,
                        post.body
                    )
                }
            }

            Button(onClick = {
                applyLocal()
                afterApplyLocal()
            }) {
                val (text, icon) =
                    if (localPost.isEmpty)
                        Pair(
                            "Delete Remote",
                            Icons.Default.Delete
                        )
                    else
                        Pair(
                            "Push Local",
                            Icons.Default.ArrowUpward
                        )
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = icon,
                    contentDescription = text
                )
                Text(text = text)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    applyRemote()
                    afterApplyRemote()
                }
            ) {
                val (text, icon) =
                    if (remotePost.isEmpty)
                        Pair(
                            "Delete Local",
                            Icons.Default.Delete
                        )
                    else
                        Pair(
                            "Fetch Remote",
                            Icons.Default.ArrowDownward
                        )
                Text(text = text)
                Icon(
                    modifier = Modifier.size(20.dp),
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
fun PostDiffCardPreview() {
    val localPost = PostData(
        12384,
        "Hello world!",
        """Local Body
          |The quick brown fox jumps over the lazy dog.
        """.trimMargin(),
        Date(),
        Date(),
    )
    val remotePost = PostData(
        12384,
        "Hello world!",
        """Remote Body
          |The quick brown fox jumps over the lazy dog.
        """.trimMargin(),
        Date(),
        Date(),
    )
    Column {
        //PostDiffCard(Optional.of(localPost), Optional.of(remotePost))
/*
        PostDiffCard(
            Optional.of(localPost), Optional.empty(),
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
        PostDiffCard(
            Optional.empty(),
            Optional.of(remotePost),
            afterApplyLocal = {},
            afterApplyRemote = {}
        )
*/
    }
}
