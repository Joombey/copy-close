package dev.farukh.copyclose.features.order.list.domain

import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.data.repos.OrderRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.order.list.data.dto.OrderState

class UpdateOrderStateUseCase(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(orderID: String, state: OrderState): Result<Unit, AppError> {
        val activeUser = userRepository.getActiveUser() ?: return Result.Error(LocalError.NoActiveUser)
        return orderRepository.updateOrderState(
            userID = activeUser.id,
            authToken = activeUser.authToken ?: return Result.Error(LocalError.NoActiveUser),
            orderID = orderID,
            state = state
        )
    }
}