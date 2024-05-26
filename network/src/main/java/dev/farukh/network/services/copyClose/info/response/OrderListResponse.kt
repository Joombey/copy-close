package dev.farukh.network.services.copyClose.info.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderListResponse (
    @SerialName("my_orders")
    val myOrders: List<OrderInfo>,
    @SerialName("to_me")
    val toMe: List<OrderInfo>
)