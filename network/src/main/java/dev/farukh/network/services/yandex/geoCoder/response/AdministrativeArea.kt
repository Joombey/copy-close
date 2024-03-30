package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdministrativeArea(
    @SerialName("AdministrativeAreaName")
    val administrativeAreaName: String,
    @SerialName("Locality")
    val locality: Locality
)