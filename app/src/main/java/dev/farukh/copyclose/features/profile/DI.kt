package dev.farukh.copyclose.features.profile

import dev.farukh.copyclose.features.profile.ui.ProfileViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider

fun profileDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider { ProfileViewModel() }
}