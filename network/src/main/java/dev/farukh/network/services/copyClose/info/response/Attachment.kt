package dev.farukh.network.services.copyClose.info.response

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: String,
    val name: String
)