package dev.farukh.copyclose.features.order.list.domain

import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.data.repos.OrderRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.network.services.copyClose.order.request.ReportRequest

class ReportUseCase(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(orderId: String, message: String): Result<Unit, AppError> {
        val user = userRepository.getActiveUser() ?: return Result.Error(LocalError.NoActiveUser)
        return orderRepository.reportOrder(
            ReportRequest(
                message = message,
                orderId = orderId,
                userId = user.id,
                authToken = user.authToken!!,
            )
        )
    }
}