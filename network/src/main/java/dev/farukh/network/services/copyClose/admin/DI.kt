package dev.farukh.network.services.copyClose.admin

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
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal val adminServiceDI by DI.Module {
    bindProvider(Tags.COPY_CLOSE_ADMIN) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.COPY_CLOSE_ADMIN) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(Tags.COPY_CLOSE_ADMIN))
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
                url("${BuildConfig.CopyCloseURL}/admin/")
            }
            WebSockets {
                maxFrameSize = 8192
                contentConverter = KotlinxWebsocketSerializationConverter(
                    instance(
                        Tags.COPY_CLOSE_ADMIN
                    )
                )
            }
        }
    }

    bindProvider<AdminService> {
        AdminServiceImpl(instance(Tags.COPY_CLOSE_ADMIN))
    }
}