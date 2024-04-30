package dev.farukh.copyclose.core.data.models

class UserInfoDTO (
    val userID: String,
    val name: String,
    val imageData: ByteArray,
    val isSeller: Boolean,
    val categories: List<ServiceCategory> = emptyList()
)