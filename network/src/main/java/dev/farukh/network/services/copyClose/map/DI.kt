package dev.farukh.network.services.copyClose.map

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.Tags
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal val mapModule by DI.Module {
    bindProvider(Tags.COPY_CLOSE_MAP) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }

    bindProvider(Tags.COPY_CLOSE_MAP) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.COPY_CLOSE_MAP))
            }
            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("BACK-COMMON", message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest { url("${BuildConfig.CopyCloseURL}/map/") }
        }
    }

    bindProvider<MapService> {
        MapServiceImpl(instance(Tags.COPY_CLOSE_MAP))
    }
}