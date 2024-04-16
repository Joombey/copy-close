package dev.farukh.network.services.copyClose.auth.response

import dev.farukh.network.core.RoleCore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterResponse(
    @SerialName("user_id")
    val userID: String,
    @SerialName("address_id")
    val addressID: String,
    @SerialName("auth_token")
    val authToken: String,
    @SerialName("user_image")
    val imageID: String,
    @SerialName("role")
    val role: RoleCore,
)