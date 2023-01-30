package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.db.AppSettingDatabase
import grpc_code_gen.token.TokenServiceGrpcKt
import io.grpc.ManagedChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.alias.i64
import unilang.type.none
import unilang.type.optional
import unilang.type.some
import java.util.*

class TokenService
internal constructor(
    channel: ManagedChannel,
    private val onNetworkErr: () -> Unit
) {
    private val stub = TokenServiceGrpcKt.TokenServiceCoroutineStub(channel)

    suspend fun getOne(uid: i64, pwd: String) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.token.get_one.req {
                this.uid = uid
                this.pwd = pwd
            }

            val result = runCatching { stub.getOne(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            if (rsp.ok)
                rsp.data.some()
            else
                none()
        }
}

class CachedTokenService
internal constructor(
    private val ctx: Context,
    private val service: TokenService,
) {
    private var tokenGetTime = Date()
    private var token = none<String>()

    private val oneMinute = 1000 * 60

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isTokenExpired() = token.isEmpty || Date().time - tokenGetTime.time > 8 * oneMinute

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getOne() =
        withContext(Dispatchers.IO) {
            val dao = AppSettingDatabase.getDatabase(ctx).appSettingDao()
            val setting = dao.get()
            val uid = setting.pilipalaUid.optional()
            val pwd = setting.pilipalaPwd.optional()

            if (isTokenExpired() && uid.isPresent && pwd.isPresent) {
                token = service.getOne(uid.get(), pwd.get())
                tokenGetTime = Date()
            }

            token
        }
}

object TokenServiceSingleton {
    private var service = none<CachedTokenService>()

    private fun reset() {
        if (service.isPresent)
            service = none()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend operator fun invoke(ctx: Context) =
        withContext(Dispatchers.IO) {
            if (service.isEmpty)
                ChannelSingleton(ctx)
                    .map { channel ->
                        service = CachedTokenService(ctx, TokenService(channel, ::reset)).some()
                    }

            service
        }
}