package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.db.AppSettingDatabase
import grpc_code_gen.token.TokenServiceGrpcKt
import io.grpc.ManagedChannel
import unilang.alias.i64
import unilang.type.none
import unilang.type.some
import java.util.*

class TokenService(
    channel: ManagedChannel
) {
    private val stub = TokenServiceGrpcKt.TokenServiceCoroutineStub(channel)

    suspend fun getOne(uid: i64, pwd: String): Optional<String> {
        val req = grpc_code_gen.token.get_one.req {
            this.uid = uid
            this.pwd = pwd
        }

        val rsp = stub.getOne(req)

        return if (rsp.ok)
            rsp.data.some()
        else
            none()
    }
}

object TokenServiceSingleton {
    private var tokenService = none<TokenService>()

    private var tokenGetTime = Date()
    private var token = none<String>()

    private const val oneMinute = 1000 * 60

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getOne(ctx: Context): Optional<String> {
        val appSettingDao = AppSettingDatabase.getDatabase(ctx).appSettingDao()
        val appSetting = appSettingDao.get()

        val token = runCatching {
            val channel = ChannelSingleton.getChannel(ctx).get()

            if (tokenService.isEmpty)
                tokenService = TokenService(channel).some()

            if (token.isEmpty || Date().time - tokenGetTime.time > 8 * oneMinute) {
                token =
                    tokenService.get().getOne(
                        appSetting.pilipalaUid ?: 0,
                        appSetting.pilipalaPwd.orEmpty()
                    )

                tokenGetTime = Date()
            }

            token
        }
        return token.getOrElse { none() }
    }
}