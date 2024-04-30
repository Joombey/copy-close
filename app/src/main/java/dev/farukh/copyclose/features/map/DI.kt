package dev.farukh.copyclose.features.map

import dev.farukh.copyclose.features.map.ui.MapViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal fun mapDI(parent: DI) = DI {
    extend(parent)
    bindProvider { MapViewModel(instance()) }
}