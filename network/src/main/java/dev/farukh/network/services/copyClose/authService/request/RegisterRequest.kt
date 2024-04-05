package dev.farukh.network.services.copyClose.authService.request

import dev.farukh.network.core.AddressCore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterRequest(
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: AddressCore,
    @SerialName("is_seller")
    val isSeller: Boolean,
)