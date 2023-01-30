package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.db.AppSettingDbSingleton
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.type.none
import unilang.type.some
import java.util.*

object ChannelSingleton {
    private var channel = none<ManagedChannel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend operator fun invoke(ctx: Context) =
        withContext(Dispatchers.IO) {
            val dao = AppSettingDbSingleton(ctx).appSettingDao()
            val appSetting = dao.get()

            if (channel.isEmpty)
                runCatching {
                    channel = ManagedChannelBuilder
                        .forAddress(appSetting.grpcHost.orEmpty(), appSetting.grpcPort ?: 0)
                        .usePlaintext()
                        .build()
                        .some()
                }

            channel
        }
}