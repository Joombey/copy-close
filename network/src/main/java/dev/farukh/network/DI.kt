package dev.farukh.network

import dev.farukh.network.services.copyClose.authService.copyCloseModule
import dev.farukh.network.services.copyClose.common.commonServiceDI
import dev.farukh.network.services.yandex.geoCoder.yandexGeoCoderModule
import dev.farukh.network.services.yandex.geoSuggester.yandexGeoSuggester
import org.kodein.di.DI

val networkDI by DI.Module {
    import(copyCloseModule)
    import(commonServiceDI)
    import(yandexGeoSuggester)
    import(yandexGeoCoderModule)
}
