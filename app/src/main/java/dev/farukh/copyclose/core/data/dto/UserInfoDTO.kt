package dev.farukh.copyclose.core.data.dto

import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.ServiceCore

class UserInfoDTO (
    val userID: String,
    val name: String,
    val imageData: ByteArray,
    val isSeller: Boolean,
    val addressCore: AddressCore,
    val services: List<ServiceCore> = emptyList()
)