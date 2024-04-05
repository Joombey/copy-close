package dev.farukh.copyclose.utils.extensions

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.utils.Result
import dev.farukh.network.utils.RequestResult

fun RequestResult.ServerError.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(NetworkError.ServerError)
}
fun RequestResult.TimeoutError.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(NetworkError.TimeoutError)
}
fun RequestResult.HostError.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(NetworkError.HostError)
}
fun RequestResult.Unknown.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(NetworkError.UnknownError(e))
}

fun RequestResult.ClientError.asUnknownError(): Result.Error<NetworkError>{
    return Result.Error(
        NetworkError.UnknownError(Exception("$code\n$errorMessage"))
    )
}