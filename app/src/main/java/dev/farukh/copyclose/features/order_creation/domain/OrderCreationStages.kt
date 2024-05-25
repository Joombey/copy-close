package dev.farukh.copyclose.features.order_creation.domain

import dev.farukh.copyclose.core.AppError

sealed interface OrderCreationStage {
    data object Success: OrderCreationStage
    data object Error: OrderCreationStage, AppError
    class LoadingFiles(val progress: Float): OrderCreationStage
    data object LoadingInfo: OrderCreationStage
}