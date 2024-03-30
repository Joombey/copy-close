package dev.farukh.copyclose.features.register

import dev.farukh.copyclose.features.register.data.RegisterRepository
import dev.farukh.copyclose.features.register.ui.RegisterViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun registerDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider {
        RegisterRepository(
            instance(),
            instance(),
            instance(),
        )
    }

    bindProvider { RegisterViewModel(instance()) }
}