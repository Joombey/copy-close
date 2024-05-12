package dev.farukh.copyclose.features.order_creation

import dev.farukh.copyclose.features.order_creation.ui.OrderCreationViewModel
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.instance

fun orderCreationDI(parentDI: DI) = DI {
    extend(parentDI)

    bindFactory<String, OrderCreationViewModel> { sellerID ->
        OrderCreationViewModel(
            sellerID = sellerID,
            userRepository = instance(),
            mediaManager = instance()
        )
    }
}