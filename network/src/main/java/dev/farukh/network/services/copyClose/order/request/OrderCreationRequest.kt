package dev.farukh.network.services.copyClose.order.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderCreationRequest(
    @SerialName("user_id")
    val userID: String,
    @SerialName("seller_id")
    val sellerID: String,
    @SerialName("auth_token")
    val authToken: String,
    @SerialName("attachments")
    val attachments: List<String>,
    @SerialName("services")
    val services: List<Pair<String, Int>>,
    @SerialName("comment")
    val comment: String? = null,
)