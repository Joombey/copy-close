package dev.farukh.copyclose.features.register

import dev.farukh.copyclose.features.register.data.repos.GeoRepository
import dev.farukh.copyclose.features.register.data.repos.MediaRepository
import dev.farukh.copyclose.features.register.domain.RegisterUseCase
import dev.farukh.copyclose.features.register.ui.RegisterViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun registerDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider { GeoRepository(instance(), instance()) }

    bindProvider { MediaRepository(instance()) }

    bindProvider { RegisterUseCase(instance(), instance(), instance()) }

    bindProvider { RegisterViewModel(instance(), instance(), instance()) }
}