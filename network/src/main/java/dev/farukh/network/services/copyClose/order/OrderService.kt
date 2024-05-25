package dev.farukh.network.services.copyClose.order

import dev.farukh.network.services.copyClose.order.request.OrderCreationRequest
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonPost
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface OrderService {
    suspend fun createOrder(request: OrderCreationRequest): RequestResult<Unit>
}

internal class OrderServiceImpl(val client: HttpClient): OrderService {
    override suspend fun createOrder(request: OrderCreationRequest) = client.commonPost(
        onResponse = {},
        config = {
            url("create")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    )
}
