package dev.farukh.copyclose.features.map

import dev.farukh.copyclose.features.map.data.repos.SellerRepository
import dev.farukh.copyclose.features.map.data.source.RemoteSellersDataSource
import dev.farukh.copyclose.features.map.ui.MapViewModel
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal fun mapDI(parent: DI) = DI {
    extend(parent)
    bindProvider { RemoteSellersDataSource(instance(), instance()) }
    bindProvider { SellerRepository(instance(), instance()) }
    bindProvider { MapViewModel(instance()) }
}