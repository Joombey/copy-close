package dev.farukh.copyclose.features.register

import dev.farukh.copyclose.features.register.data.repo.GeoRepository
import dev.farukh.copyclose.features.register.ui.RegisterViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun registerDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider {
        GeoRepository(
            instance(),
            instance(),
        )
    }

    bindProvider { RegisterViewModel(instance(), instance(), instance()) }
}