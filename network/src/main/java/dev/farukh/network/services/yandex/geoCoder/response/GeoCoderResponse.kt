package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.Serializable

@Serializable
data class GeoCoderResponse(
    val response: Response
)