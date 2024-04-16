package dev.farukh.network.services.copyClose.auth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LogInRequest(
    @SerialName("login")
    private val login: String,
    @SerialName("password")
    private val password: String,
)