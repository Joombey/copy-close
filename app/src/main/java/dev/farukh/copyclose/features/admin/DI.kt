package dev.farukh.copyclose.features.admin

import dev.farukh.copyclose.features.admin.data.repos.AdminRepository
import dev.farukh.copyclose.features.admin.ui.AdminViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun adminDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider { AdminRepository(instance()) }

    bindProvider {
        AdminViewModel(
            getOrderListUseCase = instance(),
            adminRepository = instance(),
            userRepository = instance()
        )
    }
}