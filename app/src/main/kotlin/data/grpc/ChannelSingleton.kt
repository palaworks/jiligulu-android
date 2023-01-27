package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.db.AppSettingDatabase
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import unilang.type.none
import java.util.*

object ChannelSingleton {
    private var channel: Optional<ManagedChannel> = none()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getChannel(ctx: Context): Optional<ManagedChannel> {
        val appSettingDao = AppSettingDatabase.getDatabase(ctx).appSettingDao()
        val appSetting = appSettingDao.get()

        if (channel.isEmpty)
            runCatching {
                channel = Optional.of(
                    ManagedChannelBuilder
                        .forAddress(appSetting.grpcHost.orEmpty(), appSetting.grpcPort ?: 0)
                        .usePlaintext()
                        .build()
                )
            }

        return channel
    }
}