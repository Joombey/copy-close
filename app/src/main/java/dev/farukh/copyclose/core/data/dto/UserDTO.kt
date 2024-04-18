package dev.farukh.copyclose.core.data.dto

class UserDTO(
    val id: String,
    val roleID: Int,
    val addressID: String,
    val name: String,
    val authToken: String,
    val icon: String,
    val iconUrl: String
)