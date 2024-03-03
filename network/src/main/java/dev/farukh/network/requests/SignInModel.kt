package dev.farukh.network.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignInModel(
    @SerialName("login")
    private val login: String,
    @SerialName("password")
    private val password: String,
)