package dev.farukh.copyclose.features.order.creation.domain

import dev.farukh.copyclose.core.data.repos.FileRepository
import dev.farukh.copyclose.core.data.repos.OrderRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.order.creation.data.dto.OrderCreationDTO
import kotlinx.coroutines.flow.flow

class CreateOrderUseCase(
    private val fileRepository: FileRepository,
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(data: OrderCreationDTO) = flow {
        var documentIds = emptyList<String>()
        when(val sendResult = fileRepository.sendFiles(data.attachments)) {
            is Result.Error -> {
                emit(OrderCreationStage.Error)
                return@flow
            }
            is Result.Success -> sendResult.data.collect { progress ->
                documentIds = progress.second
                emit(OrderCreationStage.LoadingFiles(progress.first))
            }
        }
        emit(OrderCreationStage.LoadingInfo)
        when(orderRepository.createOrder(data, documentIds)) {
            is Result.Error -> emit(OrderCreationStage.Error)
            is Result.Success -> emit(OrderCreationStage.Success)
        }
    }
}