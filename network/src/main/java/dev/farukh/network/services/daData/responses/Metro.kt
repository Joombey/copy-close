package dev.farukh.network.services.daData.responses

import kotlinx.serialization.Serializable

@Serializable
data class Metro(
    val distance: Double,
    val line: String,
    val name: String
)