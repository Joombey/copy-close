package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.Serializable

@Serializable
data class Envelope(
    val lowerCorner: String,
    val upperCorner: String
)