package dev.farukh.network.services.copyClose.info.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderInfo(
    @SerialName("attachments")
    val attachments: List<Attachment>,
    @SerialName("order_id")
    val orderID: String,
    @SerialName("seller_id")
    val sellerID: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("services")
    val services: List<Service>,
    @SerialName("comment")
    val comment: String,
    @SerialName("state")
    val state: Int,
    @SerialName("reported")
    val reported: Boolean,
)