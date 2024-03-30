package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostalCode(
    @SerialName("PostalCodeNumber")
    val postalCodeNumber: String
)