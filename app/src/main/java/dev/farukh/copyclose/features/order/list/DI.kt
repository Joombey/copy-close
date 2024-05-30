package dev.farukh.copyclose.features.order.list

import dev.farukh.copyclose.features.order.list.domain.GetOrderListUseCase
import dev.farukh.copyclose.features.order.list.domain.UpdateOrderStateUseCase
import dev.farukh.copyclose.features.order.list.ui.OrderListViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun orderListDI(parentDI: DI) = DI {
    extend(parentDI)
    bindProvider {
        GetOrderListUseCase(
            orderRepository = instance(),
            userRepository = instance(),
            fileRepository = instance()
        )
    }

    bindProvider {
        UpdateOrderStateUseCase(
            orderRepository = instance(),
            userRepository = instance()
        )
    }

    bindProvider {
        OrderListViewModel(
            getOrderListUseCase = instance(),
            updateOrderStateUseCase = instance()
        )
    }
}