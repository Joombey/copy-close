package dev.farukh.network.core

import kotlinx.serialization.Serializable

@Serializable
class AddressCore(
    val id: String,
    val addressName: String,
    val lat: Double,
    val lon: Double
)