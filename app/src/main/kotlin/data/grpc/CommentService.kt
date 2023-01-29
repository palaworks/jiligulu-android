package data.grpc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import data.ui.CommentData
import data.ui.PostData
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

class CommentService(
    channel: ManagedChannel,
    private val getToken: suspend () -> String
) {
    private val stub = CommentServiceGrpcKt.CommentServiceCoroutineStub(channel)

    suspend fun getOne(id: i64) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.get_one.req {
                this.token = getToken()
                this.id = id
            }

            val rsp = stub.getOne(req)

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

    suspend fun getSome(idList: List<i64>) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.get_some.req {
                this.token = getToken()
                this.idList.addAll(idList)
            }

            val rsp = stub.getSome(req)

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
            }
        }

    suspend fun getAll() =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.get_all.req {
                this.token = getToken()
            }

            val rsp = stub.getAll(req)

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
            }
        }

    suspend fun getAllSha256() =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.get_all_sha256.req {
                this.token = getToken()
            }

            val rsp = stub.getAllSha256(req)

            HashMap<i64, String>().apply {
                rsp.dataListList.forEach {
                    this[it.id] = it.sha256
                }
            }
        }

    suspend fun create(body: String, bindingId: i64, isReply: Boolean) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.create.req {
                this.token = getToken()
                this.body = body
                this.bindingId = bindingId
                this.isReply = isReply
            }

            val rsp = stub.create(req)

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

    suspend fun delete(id: i64) =
        withContext(Dispatchers.IO) {
            val req = grpc_code_gen.comment.delete.req {
                this.token = getToken()
                this.id = id
            }

            val rsp = stub.delete(req)

            rsp.ok
        }

    suspend fun update(id: i64, body: String) =
        withContext(Dispatchers.IO) {
            val req = req {
                this.token = getToken()
                this.id = id
                this.body = body
            }

            val rsp = stub.update(req)

            rsp.ok
        }

    suspend fun create(data: CommentData) = create(data.body, data.bindingId, data.isReply)

    suspend fun delete(data: CommentData) = delete(data.id)

    suspend fun update(data: CommentData) = update(data.id, data.body)
}

object CommentServiceSingleton {
    private var commentService: Optional<CommentService> = none()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getService(ctx: Context) =
        withContext(Dispatchers.IO) {
            if (commentService.isEmpty)
                runCatching {
                    val channel = ChannelSingleton.getChannel(ctx).get()
                    val getToken = suspend { TokenServiceSingleton.getOne(ctx).get() }

                    commentService = CommentService(channel, getToken).some()
                }

            commentService
        }
}
