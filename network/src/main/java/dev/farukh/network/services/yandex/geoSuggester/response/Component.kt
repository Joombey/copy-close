package dev.farukh.network.services.yandex.geoSuggester.response

import kotlinx.serialization.Serializable

@Serializable
data class Component(
    val kind: List<String>,
    val name: String
)