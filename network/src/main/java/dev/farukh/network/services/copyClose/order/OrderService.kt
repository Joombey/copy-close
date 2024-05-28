package dev.farukh.network.services.copyClose.order

import dev.farukh.network.services.copyClose.order.request.OrderCreationRequest
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import dev.farukh.network.utils.commonPost
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface OrderService {
    suspend fun createOrder(request: OrderCreationRequest): RequestResult<Unit>
    suspend fun updateOrderState(
        userId: String,
        authToken: String,
        orderId: String,
        state: Int
    ): RequestResult<Unit>
}

internal class OrderServiceImpl(val client: HttpClient) : OrderService {
    override suspend fun createOrder(request: OrderCreationRequest) = client.commonPost(
        onResponse = {},
        config = {
            url("create")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    )

    override suspend fun updateOrderState(
        userId: String,
        authToken: String,
        orderId: String,
        state: Int
    ) = client.commonGet(
        onResponse = { },
        config = {
            url {
                url("update")
                parameters["user_id"] = userId
                parameters["order_id"] = orderId
                parameters["auth_token"] = authToken
                parameters["state"] = state.toString()
            }
        }
    )
}
