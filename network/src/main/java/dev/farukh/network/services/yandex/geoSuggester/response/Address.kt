package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val component: List<Component>,
    @SerialName("formatted_address")
    val formattedAddress: String
)