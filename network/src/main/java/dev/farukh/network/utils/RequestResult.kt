package dev.farukh.network.utils

sealed class RequestResult<out R> {
    class Success<R> (val data: R) : RequestResult<R>() {
        override fun toString(): String {
            return data.toString()
        }
    }
    data class ClientError(val code: Int, val errorMessage: String): RequestResult<Nothing>()
    data object ServerError: RequestResult<Nothing>()
    data object TimeoutError: RequestResult<Nothing>()
    data object HostError: RequestResult<Nothing>()
    data class Unknown(val e: Throwable) : RequestResult<Nothing>()
}