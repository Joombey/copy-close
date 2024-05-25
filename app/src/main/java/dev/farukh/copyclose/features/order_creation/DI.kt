package dev.farukh.copyclose.features.order_creation

import dev.farukh.copyclose.features.order_creation.data.repos.OrderRepository
import dev.farukh.copyclose.features.order_creation.domain.CreateOrderUseCase
import dev.farukh.copyclose.features.order_creation.ui.OrderCreationViewModel
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun orderCreationDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider { OrderRepository(orderService = instance()) }

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