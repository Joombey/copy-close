package dev.farukh.network.di

import dev.farukh.network.services.copyClose.authService.copyCloseModule
import dev.farukh.network.services.daData.daDataModule
import dev.farukh.network.services.yandex.geoCoder.yandexGeoCoderModule
import dev.farukh.network.services.yandex.geoSuggester.yandexGeoSuggester
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider

val networkDI by DI.Module {
    import(copyCloseModule)
    import(yandexGeoSuggester)
    import(yandexGeoCoderModule)
}

internal val baseDI by DI.Module {
    bindProvider {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
}
