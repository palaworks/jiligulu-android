package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.ui.PostData
import grpc_code_gen.post.PostServiceGrpcKt
import io.grpc.ManagedChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.alias.i64
import unilang.time.Iso8601
import unilang.time.toDate
import unilang.type.none
import unilang.type.some
import java.util.*

class PostService
internal constructor(
    channel: ManagedChannel,
    tokenService: CachedTokenService,
    private val onNetworkErr: () -> Unit
) {
    private val stub = PostServiceGrpcKt.PostServiceCoroutineStub(channel)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val getToken = suspend { tokenService.getOne() }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getOne(id: i64) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.post.get_one.req {
                this.token = token.get()
                this.id = id
            }

            val result = runCatching { stub.getOne(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            if (rsp.ok)
                PostData(
                    id = rsp.data.id,
                    title = rsp.data.title,
                    body = rsp.data.body,
                    createTime = Iso8601(rsp.data.createTime).toDate(),
                    modifyTime = Iso8601(rsp.data.modifyTime).toDate()
                ).some()
            else
                none()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getSome(idList: List<i64>) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.post.get_some.req {
                this.token = token.get()
                this.idList.addAll(idList)
            }

            val result = runCatching { stub.getSome(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            rsp.dataListList.map {
                PostData(
                    it.id,
                    it.title,
                    it.body,
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

            val req = grpc_code_gen.post.get_all.req {
                this.token = token.get()
            }

            val result = runCatching { stub.getAll(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            rsp.dataListList.map {
                PostData(
                    it.id,
                    it.title,
                    it.body,
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

            val req = grpc_code_gen.post.get_all_sha256.req {
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
    suspend fun create(title: String, body: String) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext none()

            val req = grpc_code_gen.post.create.req {
                this.token = token.get()
                this.title = title
                this.body = body
            }

            val result = runCatching { stub.create(req) }
            if (result.isFailure) {
                onNetworkErr()
                return@withContext none()
            }

            val rsp = result.getOrThrow()

            if (rsp.ok)
                PostData(
                    rsp.data.id,
                    rsp.data.title,
                    rsp.data.body,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                ).some()
            else none()
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun delete(id: i64) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext false

            val req = grpc_code_gen.post.delete.req {
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
    suspend fun update(id: i64, title: String, body: String) =
        withContext(Dispatchers.IO) {
            val token = getToken()

            if (token.isEmpty)
                return@withContext false

            val req = grpc_code_gen.post.update.req {
                this.token = token.get()
                this.id = id
                this.title = title
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
    suspend fun create(data: PostData) = create(data.title, data.body)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun delete(data: PostData) = delete(data.id)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun update(data: PostData) = update(data.id, data.title, data.body)
}

object PostServiceSingleton {
    private var service: Optional<PostService> = none()

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
                        PostService(channel.get(), tokenService.get(), ::reset).some()
                    else
                        none()
            }

            service
        }
}
