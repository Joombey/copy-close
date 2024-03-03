package dev.farukh.network.utils

sealed class RequestResult<out R> {
    class Success<R> (data: R) : RequestResult<R>()
    data object ClientError: RequestResult<Nothing>()
    data object ServerInternalError: RequestResult<Nothing>()
}