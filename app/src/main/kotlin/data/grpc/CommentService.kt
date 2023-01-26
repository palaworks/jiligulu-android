package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.ui.CommentData
import grpc_code_gen.comment.CommentServiceGrpcKt
import io.grpc.ManagedChannel
import unilang.alias.i64
import unilang.time.Iso8601
import unilang.time.toDate
import java.util.*

class CommentService(
    channel: ManagedChannel,
    private val getToken: suspend () -> String
) {
    private val stub = CommentServiceGrpcKt.CommentServiceCoroutineStub(channel)

    suspend fun getOne(id: i64): Optional<CommentData> {
        val req = grpc_code_gen.comment.get_one.req {
            this.token = getToken()
            this.id = id
        }

        val rsp = stub.getOne(req)

        return if (rsp.ok) {
            Optional.of(
                CommentData(
                    id = rsp.data.id,
                    body = rsp.data.body,
                    bindingId = rsp.data.bindingId,
                    isReply = rsp.data.isReply,
                    createTime = Iso8601(rsp.data.createTime).toDate(),
                )
            )
        } else Optional.empty()
    }

    suspend fun getAll(): List<CommentData> {
        val req = grpc_code_gen.comment.get_all.req {
            token = getToken()
        }

        val rsp = stub.getAll(req)

        //TODO handle err
        return rsp.collectionList.map {
            CommentData(
                it.id,
                it.body,
                it.bindingId,
                it.isReply,
                Iso8601(it.createTime).toDate()
            )
        }
    }

    suspend fun getAllSha256(): HashMap<i64, String> {
        val req = grpc_code_gen.comment.get_all_sha256.req {
            this.token = getToken()
        }

        val rsp = stub.getAllSha256(req)

        return HashMap<i64, String>().apply {
            rsp.collectionList.forEach {
                this[it.id] = it.sha256
            }
        }
    }

    suspend fun create(body: String, bindingId: i64, isReply: Boolean): Optional<CommentData> {
        val req = grpc_code_gen.comment.create.req {
            this.token = getToken()
            this.body = body
            this.bindingId = bindingId
            this.isReply = isReply
        }

        val rsp = stub.create(req)

        return if (rsp.ok)
            Optional.of(
                CommentData(
                    rsp.data.id,
                    rsp.data.body,
                    rsp.data.bindingId,
                    rsp.data.isReply,
                    Iso8601(rsp.data.createTime).toDate()
                )
            )
        else
            Optional.empty()
    }

    suspend fun delete(id: i64): Boolean {
        val req = grpc_code_gen.comment.delete.req {
            this.token = getToken()
            this.id = id
        }

        val rsp = stub.delete(req)

        return rsp.ok
    }

    suspend fun update(id: i64, body: String): Optional<CommentData> {
        val req = grpc_code_gen.comment.update.req {
            this.token = getToken()
            this.id = id
            this.body = body
        }

        val rsp = stub.update(req)

        return if (rsp.ok)
            Optional.of(
                CommentData(
                    rsp.data.id,
                    rsp.data.body,
                    rsp.data.bindingId,
                    rsp.data.isReply,
                    Iso8601(rsp.data.createTime).toDate()
                )
            )
        else
            Optional.empty()
    }
}

object CommentServiceSingleton {
    private var commentService: Optional<CommentService> = Optional.empty()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getService(ctx: Context): Optional<CommentService> {
        if (commentService.isEmpty)
            runCatching {
                val channel = ChannelSingleton.getChannel(ctx).get()
                val getToken = suspend { TokenServiceSingleton.getOne(ctx).get() }

                commentService = Optional.of(CommentService(channel, getToken))
            }

        return commentService
    }
}
