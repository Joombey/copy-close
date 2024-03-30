package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoCoderAddress(
    @SerialName("Components")
    val geoCoderComponents: List<GeoCoderComponent>,
    @SerialName("country_code")
    val countryCode: String,
    val formatted: String,
    @SerialName("postal_code")
    val postalCode: String
)