package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Thoroughfare(
    @SerialName("Premise")
    val premise: Premise,
    @SerialName("ThoroughfareName")
    val thoroughfareName: String
)