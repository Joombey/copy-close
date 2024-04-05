package dev.farukh.network.services.copyClose.authService

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

internal val copyCloseModule by DI.Module {
    bindProvider(Tags.COPY_CLOSE) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.COPY_CLOSE) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.COPY_CLOSE))
            }
            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("BACK", "log: $message")
                    }
                }
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url(BuildConfig.CopyCloseURL)
            }
        }
    }

    bindProvider<AuthService> {
        AuthServiceImpl(instance(Tags.COPY_CLOSE), instance(Tags.COPY_CLOSE))
    }
}