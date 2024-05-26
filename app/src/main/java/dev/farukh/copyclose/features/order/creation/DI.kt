package dev.farukh.copyclose.features.order.creation

import dev.farukh.copyclose.features.order.creation.domain.CreateOrderUseCase
import dev.farukh.copyclose.features.order.creation.ui.OrderCreationViewModel
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun orderCreationDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider {
        CreateOrderUseCase(
            fileRepository = instance(),
            orderRepository = instance()
        )
    }

    bindFactory<String, OrderCreationViewModel> { sellerID ->
        OrderCreationViewModel(
            sellerID = sellerID,
            userRepository = instance(),
            mediaManager = instance(),
            createOrderUseCase = instance()
        )
    }
}