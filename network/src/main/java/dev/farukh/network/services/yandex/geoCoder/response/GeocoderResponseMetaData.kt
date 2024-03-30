package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.Serializable

@Serializable
data class GeocoderResponseMetaData(
    val found: Int,
    val request: String,
    val results: Int
)