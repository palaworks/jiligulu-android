package component

import data.db.AppSettingDatabase
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Settings(
    contentPadding: PaddingValues
) {
    val appSettingDao = AppSettingDatabase.getDatabase(LocalContext.current).appSettingDao()
    var appSetting by remember { mutableStateOf(appSettingDao.get()) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth()
    ) {
        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            text = "gRPC channel"
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Host",
            "E.g., https://for.example.domain",
            false,
            appSetting.grpcHost.toString(),
        ) {
            appSettingDao.update(appSetting.copy(grpcHost = it))
            appSetting = appSettingDao.get()
        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Port",
            "E.g., 40040",
            false,
            appSetting.grpcPort.toString()
        ) {
            appSettingDao.update(appSetting.copy(grpcPort = it))
            appSetting = appSettingDao.get()
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            text = "pilipala account"
        )
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "User id",
            "E.g., 1001",
            false,
            appSetting.pilipalaUid.toString()
        ) {
            appSettingDao.update(appSetting.copy(pilipalaUid = it))
            appSetting = appSettingDao.get()
        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Password",
            "E.g., 114514",
            true,
            appSetting.pilipalaPwd.toString()
        ) {
            appSettingDao.update(appSetting.copy(pilipalaPwd = it))
            appSetting = appSettingDao.get()
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    Settings(PaddingValues(0.dp))
}
