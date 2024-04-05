package dev.farukh.network.services.yandex.geoCoder

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.Tags
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal val yandexGeoCoderModule by DI.Module {
    bindProvider(Tags.YANDEX_GEO_CODER) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.YANDEX_GEO_CODER) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.YANDEX_GEO_CODER))
            }
            install(DefaultRequest) {
                url {
                    url(BuildConfig.GeoCoderUrl)
                    parameters["apikey"] = BuildConfig.GeoCoderApiKey
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

    bindProvider<YandexGeoCoderService> {
        YandexGeoCoderServiceImpl(instance(Tags.YANDEX_GEO_CODER))
    }
}