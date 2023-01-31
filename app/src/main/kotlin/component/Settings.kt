package component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.db.AppSetting
import data.db.AppSettingDatabase
import data.db.AppSettingDbSingleton
import data.grpc.ChannelSingleton
import data.grpc.CommentServiceSingleton
import data.grpc.PostServiceSingleton
import data.grpc.TokenServiceSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import ui.state.CommentListScreenViewModelSingleton
import ui.state.PostListScreenViewModelSingleton
import unilang.type.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Settings(
    contentPadding: PaddingValues
) {
    val ctx = LocalContext.current
    var setting by rememberMutStateOf(none<AppSetting>())

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch(Dispatchers.IO) {
        val dao = AppSettingDbSingleton(ctx).appSettingDao()
        setting = dao.get().some()
    }

    fun save(data: AppSetting) =
        coroutineScope.launch(Dispatchers.IO) {
            val dao = AppSettingDbSingleton(ctx).appSettingDao()
            dao.update(data)
            setting = dao.get().some()
            TokenServiceSingleton.reset()
            PostServiceSingleton.reset()
            PostListScreenViewModelSingleton.reset()
            CommentServiceSingleton.reset()
            CommentListScreenViewModelSingleton.reset()
        }

    if (setting.isPresent)
        Column(
            modifier = FillMaxWidthModifier
                .padding(contentPadding)
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold,
                text = "gRPC channel"
            )
            Spacer(Modifier.height(20.dp))
            SettingItem(
                onSave = { new ->
                    setting.map { old ->
                        if (old.grpcHost != new)
                            save(old.copy(grpcHost = new))
                    }
                },
                "Host",
                "E.g., https://for.example.domain",
                false,
                setting.get().grpcHost.orEmpty(),
            )
            Spacer(Modifier.height(20.dp))
            SettingItem(
                onSave = { new ->
                    setting.map { old ->
                        if (old.grpcPort != new.toInt())
                            save(old.copy(grpcPort = new.toInt()))
                    }
                },
                "Port",
                "E.g., 40040",
                false,
                setting.get().grpcPort.map { it.toString() }.or("")
            )

            Spacer(Modifier.height(40.dp))

            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold,
                text = "pilipala account"
            )
            Spacer(Modifier.height(20.dp))
            SettingItem(
                onSave = { new ->
                    setting.map { old ->
                        if (old.pilipalaUid != new.toLong())
                            save(old.copy(pilipalaUid = new.toLong()))
                    }
                },
                "User id",
                "E.g., 1001",
                false,
                setting.get().pilipalaUid.map { it.toString() }.or("")
            )
            Spacer(Modifier.height(20.dp))
            SettingItem(
                onSave = { new ->
                    setting.map { old ->
                        if (old.pilipalaPwd != new)
                            save(old.copy(pilipalaPwd = new))
                    }
                },
                "Password",
                "E.g., 114514",
                true,
                setting.get().pilipalaPwd.orEmpty(),
            )
        }
}
