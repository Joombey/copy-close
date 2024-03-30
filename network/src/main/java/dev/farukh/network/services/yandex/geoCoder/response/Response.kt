package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)