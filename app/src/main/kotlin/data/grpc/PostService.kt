package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import data.ui.PostData
import unilang.alias.i64
import io.grpc.ManagedChannel
import grpc_code_gen.post.PostServiceGrpcKt
import unilang.time.Iso8601
import unilang.time.toDate
import kotlin.collections.HashMap

class PostService(
    channel: ManagedChannel,
    private val getToken: suspend () -> String
) {
    private val stub = PostServiceGrpcKt.PostServiceCoroutineStub(channel)

    suspend fun getOne(id: i64): Optional<PostData> {
        val req = grpc_code_gen.post.get_one.req {
            this.token = getToken()
            this.id = id
        }

        val rsp = stub.getOne(req)

        return if (rsp.ok)
            Optional.of(
                PostData(
                    id = rsp.data.id,
                    title = rsp.data.title,
                    body = rsp.data.body,
                    createTime = Iso8601(rsp.data.createTime).toDate(),
                    modifyTime = Iso8601(rsp.data.modifyTime).toDate()
                )
            )
        else
            Optional.empty()
    }

    suspend fun getAll(): List<PostData> {
        val req = grpc_code_gen.post.get_all.req {
            this.token = getToken()
        }

        val rsp = stub.getAll(req)

        return rsp.collectionList.map {
            PostData(
                it.id,
                it.title,
                it.body,
                Iso8601(it.createTime).toDate(),
                Iso8601(it.modifyTime).toDate()
            )
        }
    }

    suspend fun getAllSha256(): HashMap<i64, String> {
        val req = grpc_code_gen.post.get_all_sha256.req {
            this.token = getToken()
        }

        val rsp = stub.getAllSha256(req)

        return HashMap<i64, String>().apply {
            rsp.collectionList.forEach {
                this[it.id] = it.sha256
            }
        }
    }

    suspend fun create(title: String, body: String): Optional<PostData> {
        val req = grpc_code_gen.post.create.req {
            this.token = getToken()
            this.title = title
            this.body = body
        }

        val rsp = stub.create(req)

        return if (rsp.ok)
            Optional.of(
                PostData(
                    rsp.data.id,
                    rsp.data.title,
                    rsp.data.body,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                )
            )
        else
            Optional.empty()
    }

    suspend fun delete(id: i64): Boolean {
        val req = grpc_code_gen.post.delete.req {
            this.token = getToken()
            this.id = id
        }

        val rsp = stub.delete(req)

        return rsp.ok
    }

    suspend fun update(id: i64, title: String, body: String): Optional<PostData> {
        val req = grpc_code_gen.post.update.req {
            this.token = getToken()
            this.id = id
            this.title = title
            this.body = body
        }

        val rsp = stub.update(req)

        return if (rsp.ok)
            Optional.of(
                PostData(
                    rsp.data.id,
                    rsp.data.title,
                    rsp.data.body,
                    Iso8601(rsp.data.createTime).toDate(),
                    Iso8601(rsp.data.modifyTime).toDate()
                )
            )
        else
            Optional.empty()
    }
}

object PostServiceSingleton {
    private var postService: Optional<PostService> = Optional.empty()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getService(ctx: Context): Optional<PostService> {
        if (postService.isEmpty)
            runCatching {
                val channel = ChannelSingleton.getChannel(ctx).get()
                val getToken = suspend { TokenServiceSingleton.getOne(ctx).get() }

                postService = Optional.of(PostService(channel, getToken))
            }

        return postService
    }
}
