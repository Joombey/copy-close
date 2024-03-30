package dev.farukh.network.services.yandex.geoSuggester

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.di.Tags
import dev.farukh.network.di.baseDI
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal val yandexGeoSuggester by DI.Module {
    bindProvider(Tags.YANDEX_GEO_SUGGESTER) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }

    bindProvider(Tags.YANDEX_GEO_SUGGESTER) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(instance(Tags.YANDEX_GEO_SUGGESTER))
            }
            install(DefaultRequest) {
                url {
                    url(BuildConfig.GeoSuggesterUrl)
                    parameters["apikey"] = BuildConfig.GeoSuggesterApiKey
                }
            }
            Logging {
                logger = object: Logger {
                    override fun log(message: String) {
                        Log.i("YANDEX_GEO", message)
                    }
                }
                level = LogLevel.ALL
            }
            expectSuccess = true
        }
    }

    bindProvider<YandexGeoSuggesterService> {
        YandexGeoSuggesterServiceImpl(instance(Tags.YANDEX_GEO_SUGGESTER))
    }
}