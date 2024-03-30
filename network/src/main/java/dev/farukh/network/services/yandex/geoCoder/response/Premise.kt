package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Premise(
    @SerialName("PostalCode")
    val postalCode: PostalCode,
    @SerialName("PremiseNumber")
    val premiseNumber: String
)