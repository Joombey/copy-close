package dev.farukh.network.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignUpModel(
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double
)