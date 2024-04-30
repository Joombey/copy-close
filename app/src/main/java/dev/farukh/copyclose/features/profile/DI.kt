package dev.farukh.copyclose.features.profile

import dev.farukh.copyclose.features.profile.ui.ProfileViewModel
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.instance

fun profileDI(parentDI: DI) = DI {
    extend(parentDI)

    bindFactory<String, ProfileViewModel> { userID ->
        ProfileViewModel(instance(), userID)
    }
}