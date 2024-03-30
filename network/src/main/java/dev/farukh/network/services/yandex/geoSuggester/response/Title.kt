package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.Serializable

@Serializable
data class Title(
    val hl: List<Hl>,
    val text: String
)