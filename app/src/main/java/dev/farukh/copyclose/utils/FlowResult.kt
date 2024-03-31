package dev.farukh.copyclose.utils

sealed class FlowResult<out S, out E> {
    class Success<S>(val data: S): FlowResult<S, Nothing>()
    class Error<E: Throwable>(val err: E): FlowResult<Nothing, E>()

    companion object {
        fun networkErr(netErr: NetErr) = Error(netErr)
    }
}

sealed class NetErr(msg: String): Exception(msg) {
    class ClientErrorException(): NetErr("")
    class ServerInternalError(): NetErr("")
}