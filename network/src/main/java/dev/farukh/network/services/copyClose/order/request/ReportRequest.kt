package dev.farukh.network.services.copyClose.order.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/*
OrderID   uuid.UUID `json:"order_id"`
	Message   string    `json:"message"`
	UserID    uuid.UUID `json:"user_id"`
	AuthToken uuid.UUID `json:"auth_token"`
 */

@Serializable
class ReportRequest(
    @SerialName("order_id")
    val orderId: String,
    @SerialName("message")
    val message: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("auth_token")
    val authToken: String
)