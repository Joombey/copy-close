package dev.farukh.copyclose.core


interface AppError

interface NetworkError: AppError {
    data object ServerError: NetworkError
    data object TimeoutError: NetworkError
    data object HostError: NetworkError
    class UnknownError(val throwable: Throwable): NetworkError
}

interface ClientError: NetworkError

sealed interface AuthError: ClientError {
    data object LoginError: AuthError
    data object AuthTokenError: AuthError
}

sealed interface ResourceError: ClientError {
    data object NotFoundError: ResourceError
}

sealed interface LocalError: AppError {
    data object NoActiveUser: LocalError
}