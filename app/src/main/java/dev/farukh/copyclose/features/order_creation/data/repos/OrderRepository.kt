package dev.farukh.copyclose.features.order_creation.data.repos

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.data.dto.OrderCreationDTO
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asResult
import dev.farukh.network.services.copyClose.order.OrderService
import dev.farukh.network.services.copyClose.order.request.OrderCreationRequest

class OrderRepository(private val orderService: OrderService) {
    suspend fun createOrder(
        dto: OrderCreationDTO,
        attachmentIds: List<String>,
    ): Result<Unit, NetworkError> {
        val createResult = orderService.createOrder(
            OrderCreationRequest(
                userID = dto.userID,
                sellerID = dto.sellerID,
                attachments = attachmentIds,
                services = dto.services,
                comment = dto.comment,
                authToken = dto.authToken
            )
        )
        return createResult.asResult()
    }
}