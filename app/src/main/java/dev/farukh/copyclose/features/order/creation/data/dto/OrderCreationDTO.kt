package dev.farukh.copyclose.features.order.creation.data.dto

import dev.farukh.copyclose.core.data.models.MediaInfo

class OrderCreationDTO(
    val userID: String,
    val authToken: String,
    val sellerID: String,
    val comment: String,
    val attachments: List<MediaInfo>,
    val services: List<Pair<String, Int>>
)