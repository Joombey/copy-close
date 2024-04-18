package dev.farukh.copyclose.core.utils

sealed class Result<out S, out E> {
    class Success<S>(val data: S): Result<S, Nothing>()
    class Error<E>(val data: E): Result<Nothing, E>()
}