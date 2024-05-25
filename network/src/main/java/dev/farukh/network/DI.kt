package dev.farukh.network

import dev.farukh.network.services.copyClose.auth.copyCloseModule
import dev.farukh.network.services.copyClose.file.fileServiceDI
import dev.farukh.network.services.copyClose.info.infoServiceDI
import dev.farukh.network.services.copyClose.map.mapServiceDI
import dev.farukh.network.services.copyClose.order.orderServiceDI
import dev.farukh.network.services.copyClose.profile.profileServiceDI
import dev.farukh.network.services.yandex.geoCoder.yandexGeoCoderModule
import dev.farukh.network.services.yandex.geoSuggester.yandexGeoSuggester
import org.kodein.di.DI

val networkDI by DI.Module {
    import(copyCloseModule)
    import(fileServiceDI)
    import(infoServiceDI)
    import(mapServiceDI)
    import(yandexGeoSuggester)
    import(yandexGeoCoderModule)
    import(profileServiceDI)
    import(orderServiceDI)
}
