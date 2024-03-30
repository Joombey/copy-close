package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.Serializable

@Serializable
data class Distance(
    val text: String,
    val value: Double
)