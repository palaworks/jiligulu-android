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
import kotlinx.coroutines.launch
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
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

    coroutineScope.launch {
        val dao = AppSettingDatabase.getDatabase(ctx).appSettingDao()
        setting = dao.get().some()
    }

    fun save(data: AppSetting) {
        coroutineScope.launch {
            val dao = AppSettingDatabase.getDatabase(ctx).appSettingDao()
            dao.update(data)
            setting = dao.get().some()
        }
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
                "Host",
                "E.g., https://for.example.domain",
                false,
                setting.get().grpcHost.orEmpty(),
            ) {
                setting.map { old ->
                    save(old.copy(grpcHost = it))
                }
            }
            Spacer(Modifier.height(20.dp))
            SettingItem(
                "Port",
                "E.g., 40040",
                false,
                setting.get().grpcPort.map { it.toString() }.or("")
            ) {
                setting.map { old ->
                    save(old.copy(grpcPort = it.toInt()))
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold,
                text = "pilipala account"
            )
            Spacer(Modifier.height(20.dp))
            SettingItem(
                "User id",
                "E.g., 1001",
                false,
                setting.get().pilipalaUid.map { it.toString() }.or("")
            ) {
                setting.map { old ->
                    save(old.copy(pilipalaUid = it.toLong()))
                }
            }
            Spacer(Modifier.height(20.dp))
            SettingItem(
                "Password",
                "E.g., 114514",
                true,
                setting.get().pilipalaPwd.orEmpty(),
            ) {
                setting.map { old ->
                    save(old.copy(pilipalaPwd = it))
                }
            }
        }
}
