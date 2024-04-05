package dev.farukh.copyclose.utils.extensions

val Boolean.long get() = run {
    if (this) {
        1L
    } else {
        0L
    }
}

val Number.bool get() = this == 1