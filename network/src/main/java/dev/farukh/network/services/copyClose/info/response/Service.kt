package dev.farukh.network.services.copyClose.info.response

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val amount: Int,
    val id: String,
    val price: Int,
    val title: String
)