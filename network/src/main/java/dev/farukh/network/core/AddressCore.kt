package dev.farukh.network.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AddressCore(
    val id: String? = null,
    @SerialName("address")
    val addressName: String,
    val lat: Double,
    val lon: Double
)