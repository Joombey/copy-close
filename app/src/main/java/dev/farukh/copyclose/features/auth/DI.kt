package dev.farukh.copyclose.features.auth

import dev.farukh.copyclose.features.auth.domain.LoginUseCase
import dev.farukh.copyclose.features.auth.ui.AuthViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun authDI(parentDI: DI) = DI {
    extend(parentDI)
    bindProvider { LoginUseCase(instance(), instance()) }
    bindProvider { AuthViewModel(instance()) }
}