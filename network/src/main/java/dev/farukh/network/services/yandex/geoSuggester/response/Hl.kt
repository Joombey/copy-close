package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.Serializable

@Serializable
data class Hl(
    val begin: Int,
    val end: Int
)