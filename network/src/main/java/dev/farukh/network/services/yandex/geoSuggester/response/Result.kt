package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
//    @SerialName("address")
//    val address: Address,
    @SerialName("distance")
    val distance: Distance,
    @SerialName("subtitle")
    val subtitle: Subtitle,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("title")
    val title: Title,
    @SerialName("uri")
    val uri: String
)