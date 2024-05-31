package dev.farukh.network.services.copyClose.admin.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class BlockResponse(
    @SerialName("report_id")
    val reportId: String,
    @SerialName("order_id")
    val orderId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("seller_id")
    val sellerId: String,
    @SerialName("report_message")
    val reportMessage: String? = "",
    @SerialName("order_message")
    val orderMessage: String,
    @SerialName("report_date")
    val reportDate: String,
)