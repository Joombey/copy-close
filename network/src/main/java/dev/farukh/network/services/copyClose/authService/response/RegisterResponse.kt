package dev.farukh.network.services.copyClose.authService.response

import dev.farukh.network.core.RoleCore
import kotlinx.serialization.Serializable

@Serializable
class RegisterResponse(
    val userID: String,
    val addressID: String,
    val authToken: String,
    val imageUrl: String,
    val role: RoleCore,
)