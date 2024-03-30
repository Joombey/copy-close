package dev.farukh.network.services.daData.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressSuggestion(
    @SerialName("city")
    val city: String?,
    @SerialName("geo_lat")
    val geoLat: Double,
    @SerialName("geo_lon")
    val geoLon: Double,
    @SerialName("result")
    val result: String?,
    @SerialName("metro")
    val metro: List<Metro>
)