package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeCoderCountry(
    @SerialName("AddressLine")
    val addressLine: String,
    @SerialName("AdministrativeArea")
    val administrativeArea: AdministrativeArea,
    @SerialName("CountryName")
    val countryName: String,
    @SerialName("CountryNameCode")
    val countryNameCode: String
)