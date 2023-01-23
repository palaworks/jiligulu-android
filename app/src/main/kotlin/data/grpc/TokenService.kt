package data.grpc

import grpc_code_gen.token.TokenServiceGrpcKt
import io.grpc.ManagedChannel
import unilang.alias.i64
import java.util.Optional

class TokenService(channel: ManagedChannel) {
    private val stub = TokenServiceGrpcKt.TokenServiceCoroutineStub(channel)

    suspend fun getOne(uid: i64, pwd: String): Optional<String> {
        val req = grpc_code_gen.token.get_one.req {
            this.uid = uid
            this.pwd = pwd
        }

        val rsp = stub.getOne(req)

        return if (rsp.ok)
            Optional.of(rsp.value)
        else
            Optional.empty()
    }
}