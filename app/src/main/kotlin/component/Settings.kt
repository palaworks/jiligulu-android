package component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.db.AppSettingDatabase
import ui.FillMaxWidthModifier
import ui.rememberMutStateOf
import unilang.type.map
import unilang.type.or

@Composable
fun Settings(
    contentPadding: PaddingValues
) {
    val appSettingDao = AppSettingDatabase.getDatabase(LocalContext.current).appSettingDao()
    var appSetting by rememberMutStateOf(appSettingDao.get())

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
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Host",
            "E.g., https://for.example.domain",
            false,
            appSetting.grpcHost.or(""),
        ) {
            appSettingDao.update(appSetting.copy(grpcHost = it))
            appSetting = appSettingDao.get()
        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Port",
            "E.g., 40040",
            false,
            appSetting.grpcPort.map { it.toString() }.or("")
        ) {
            appSettingDao.update(appSetting.copy(grpcPort = it.toInt()))
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
            appSetting.pilipalaUid.map { it.toString() }.or("")
        ) {
            appSettingDao.update(appSetting.copy(pilipalaUid = it.toLong()))
            appSetting = appSettingDao.get()
        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingItem(
            "Password",
            "E.g., 114514",
            true,
            appSetting.pilipalaPwd.or("")
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
