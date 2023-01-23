package data.grpc

import data.ui.CommentData
import data.ui.PostData
import grpc_code_gen.comment.CommentServiceGrpcKt
import io.grpc.ManagedChannel
import unilang.alias.i64
import unilang.time.Iso8601
import unilang.time.toDate
import java.util.*

class CommentService(channel: ManagedChannel, private val getToken: () -> String) {
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
                Iso8601(it.createTime).toDate()
            )
        }
    }

    suspend fun getAllSha256(): List<Pair<i64, String>> {
        val req = grpc_code_gen.comment.get_all_sha256.req {
            this.token = getToken()
        }

        val rsp = stub.getAllSha256(req)

        return rsp.collectionList.map {
            Pair(it.id, it.sha256)
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
                    Iso8601(rsp.data.createTime).toDate()
                )
            )
        else
            Optional.empty()
    }
}