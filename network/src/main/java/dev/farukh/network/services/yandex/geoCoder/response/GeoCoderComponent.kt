package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.Serializable

@Serializable
data class GeoCoderComponent(
    val kind: String,
    val name: String
)