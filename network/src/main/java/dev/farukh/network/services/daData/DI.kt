package dev.farukh.network.services.daData

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.di.Tags
import dev.farukh.network.di.baseDI
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

internal val daDataModule by DI.Module {
    bindProvider(Tags.DA_DATA) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.DA_DATA) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.DA_DATA))
            }
            install(DefaultRequest) {
//                headers["Authorization"] = "Token ${BuildConfig.DaDataApiKey}"
//                headers["X-Secret"] = BuildConfig.DaDataSecret
                url(BuildConfig.DaDataURL)
            }
            Logging {
                logger = object: Logger {
                    override fun log(message: String) {
                        Log.i("DA_DATA", message)
                    }
                }
                level = LogLevel.ALL
            }
            expectSuccess = true
        }
    }

    bindProvider<DaDataService> {
        DaDataServiceImpl(instance(Tags.DA_DATA), instance(Tags.DA_DATA))
    }
}