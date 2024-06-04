package dev.farukh.network.services.copyClose.order

import dev.farukh.network.services.copyClose.order.request.OrderCreationRequest
import dev.farukh.network.services.copyClose.order.request.ReportRequest
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import dev.farukh.network.utils.commonPost
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface OrderService {
    suspend fun createOrder(request: OrderCreationRequest): RequestResult<Unit>
    suspend fun sendReport(request: ReportRequest): RequestResult<Unit>
    suspend fun updateOrderState(
        userId: String,
        authToken: String,
        orderId: String,
        state: Int
    ): RequestResult<Unit>

    suspend fun listen(): Flow<Unit>
}

internal class OrderServiceImpl(val client: HttpClient) : OrderService {

    override suspend fun sendReport(request: ReportRequest): RequestResult<Unit> =
        client.commonPost(
            onResponse = {},
            config = {
                contentType(ContentType.Application.Json)
                setBody(request)
                url("report")
            }
        )

    override suspend fun createOrder(request: OrderCreationRequest) =
        client.commonPost(
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

    override suspend fun listen(): Flow<Unit> = flow {
        client.webSocket(
            request = {
                url("listen")
            },
            block = {
                for (frame in incoming) {
                    emit(Unit)
                }
            }
        )
    }
}
