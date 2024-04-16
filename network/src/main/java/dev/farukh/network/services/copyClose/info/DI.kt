package dev.farukh.network.services.copyClose.info

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

internal val infoServiceDI by DI.Module {
    bindProvider(Tags.COPY_CLOSE_INFO) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.COPY_CLOSE_INFO) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.COPY_CLOSE_INFO))
            }
            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("BACK-COMMON", message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest { url("${BuildConfig.CopyCloseURL}/info/") }
        }
    }

    bindProvider<InfoService> {
        InfoServiceImpl(instance(Tags.COPY_CLOSE_INFO), instance(Tags.COPY_CLOSE_INFO))
    }
}