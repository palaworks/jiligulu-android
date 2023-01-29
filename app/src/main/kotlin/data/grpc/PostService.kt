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

class PostService(
    channel: ManagedChannel,
    private val getToken: suspend () -> String
) {
    private val stub = PostServiceGrpcKt.PostServiceCoroutineStub(channel)

    suspend fun getOne(id: i64) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.get_one.req {
                this.token = getToken()
                this.id = id
            }

            val rsp = stub.getOne(req)

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

    suspend fun getAll() =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.get_all.req {
                this.token = getToken()
            }

            val rsp = stub.getAll(req)

            rsp.collectionList.map {
                PostData(
                    it.id,
                    it.title,
                    it.body,
                    Iso8601(it.createTime).toDate(),
                    Iso8601(it.modifyTime).toDate()
                )
            }
        }

    suspend fun getAllSha256() =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.get_all_sha256.req {
                this.token = getToken()
            }

            val rsp = stub.getAllSha256(req)

            HashMap<i64, String>().apply {
                rsp.collectionList.forEach {
                    this[it.id] = it.sha256
                }
            }
        }

    suspend fun create(title: String, body: String) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.create.req {
                this.token = getToken()
                this.title = title
                this.body = body
            }

            val rsp = stub.create(req)

            if (rsp.ok)
                PostData(
                    rsp.data.id,
                    rsp.data.title,
                    rsp.data.body,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                ).some()
            else
                none()
        }

    suspend fun delete(id: i64) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.delete.req {
                this.token = getToken()
                this.id = id
            }

            val rsp = stub.delete(req)

            rsp.ok
        }

    suspend fun update(id: i64, title: String, body: String) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.post.update.req {
                this.token = getToken()
                this.id = id
                this.title = title
                this.body = body
            }

            val rsp = stub.update(req)

            if (rsp.ok)
                PostData(
                    rsp.data.id,
                    rsp.data.title,
                    rsp.data.body,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                ).some()
            else
                none()
        }

    suspend fun create(data: PostData) = create(data.title, data.body)

    suspend fun delete(data: PostData) = delete(data.id)

    suspend fun update(data: PostData) = update(data.id, data.title, data.body)
}

object PostServiceSingleton {
    private var postService: Optional<PostService> = none()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getService(ctx: Context) =
        withContext(Dispatchers.IO) {
            if (postService.isEmpty)
                runCatching {
                    val channel = ChannelSingleton.getChannel(ctx).get()
                    val getToken = suspend { TokenServiceSingleton.getOne(ctx).get() }

                    postService = PostService(channel, getToken).some()
                }

            postService
        }
}
