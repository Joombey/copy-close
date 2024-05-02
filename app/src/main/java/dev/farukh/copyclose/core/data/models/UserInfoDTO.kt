package dev.farukh.copyclose.core.data.models

import dev.farukh.network.core.ServiceCore

class UserInfoDTO (
    val userID: String,
    val name: String,
    val imageData: ByteArray,
    val isSeller: Boolean,
    val services: List<ServiceCore> = emptyList()
)