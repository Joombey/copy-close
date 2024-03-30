package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexGeoSuggestResponse(
    @SerialName("results")
    val results: List<Result>
)