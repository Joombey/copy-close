package dev.farukh.copyclose.core.utils.extensions

import dev.farukh.network.utils.RequestResult

val Boolean.long get() = run {
    if (this) {
        1L
    } else {
        0L
    }
}

val Number.bool get() = this == 1

val <T> Iterable<RequestResult<T>>.hasUnSuccess get() = any { it !is RequestResult.Success }
val <T> Sequence<RequestResult<T>>.hasUnSuccess get() = any { it !is RequestResult.Success }