package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.ui.CommentData
import grpc_code_gen.comment.CommentServiceGrpcKt
import grpc_code_gen.comment.update.req
import io.grpc.ManagedChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.alias.i64
import unilang.time.Iso8601
import unilang.time.toDate
import unilang.type.none
import unilang.type.some
import java.util.*

class CommentService
internal constructor(
    channel: ManagedChannel,
    tokenService: CachedTokenService,
    private val onNetworkErr: () -> Unit
) {
    private val stub = CommentServiceGrpcKt.CommentServiceCoroutineStub(channel)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val getToken = suspend { tokenService.getOne() }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getOne(id: i64) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.comment.get_one.req {
                this.token = token.get()
                this.id = id
            }

            val result = runCatching { stub.getOne(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            if (rsp.ok) {
                CommentData(
                    id = rsp.data.id,
                    body = rsp.data.body,
                    bindingId = rsp.data.bindingId,
                    isReply = rsp.data.isReply,
                    createTime = Iso8601(rsp.data.createTime).toDate(),
                    modifyTime = Iso8601(rsp.data.modifyTime).toDate(),
                ).some()
            } else none()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getSome(idList: List<i64>) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.comment.get_some.req {
                this.token = token.get()
                this.idList.addAll(idList)
            }

            val result = runCatching { stub.getSome(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            //TODO handle err
            rsp.dataListList.map {
                CommentData(
                    it.id,
                    it.body,
                    it.bindingId,
                    it.isReply,
                    Iso8601(it.createTime).toDate(),
                    Iso8601(it.modifyTime).toDate()
                )
            }.some()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getAll() =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.comment.get_all.req {
                this.token = token.get()
            }

            val result = runCatching { stub.getAll(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            //TODO handle err
            rsp.dataListList.map {
                CommentData(
                    it.id,
                    it.body,
                    it.bindingId,
                    it.isReply,
                    Iso8601(it.createTime).toDate(),
                    Iso8601(it.modifyTime).toDate()
                )
            }.some()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getAllSha256() =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.comment.get_all_sha256.req {
                this.token = token.get()
            }

            val result = runCatching { stub.getAllSha256(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            HashMap<i64, String>().apply {
                rsp.dataListList.forEach {
                    this[it.id] = it.sha256
                }
            }.some()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun create(body: String, bindingId: i64, isReply: Boolean) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.comment.create.req {
                this.token = token.get()
                this.body = body
                this.bindingId = bindingId
                this.isReply = isReply
            }

            val result = runCatching { stub.create(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            if (rsp.ok)
                CommentData(
                    rsp.data.id,
                    rsp.data.body,
                    rsp.data.bindingId,
                    rsp.data.isReply,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                ).some()
            else
                none()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun delete(id: i64) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext false

            val req = grpc_code_gen.comment.delete.req {
                this.token = token.get()
                this.id = id
            }

            val result = runCatching { stub.delete(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext false
            }

            val rsp = result.getOrThrow()

            rsp.ok
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun update(id: i64, body: String) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext false

            val req = req {
                this.token = token.get()
                this.id = id
                this.body = body
            }

            val result = runCatching { stub.update(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext false
            }

            val rsp = result.getOrThrow()

            rsp.ok
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun create(data: CommentData) = create(data.body, data.bindingId, data.isReply)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun delete(data: CommentData) = delete(data.id)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun update(data: CommentData) = update(data.id, data.body)
}

object CommentServiceSingleton {
    private var service: Optional<CommentService> = none()

    private fun reset() {
        if (service.isPresent)
            service = none()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend operator fun invoke(ctx: Context) =
        withContext(Dispatchers.IO) {
            if (service.isEmpty) {
                val channel = ChannelSingleton(ctx)
                val tokenService = TokenServiceSingleton(ctx)

                service =
                    if (channel.isPresent && tokenService.isPresent)
                        CommentService(channel.get(), tokenService.get(), ::reset).some()
                    else
                        none()
            }

            service
        }
}
