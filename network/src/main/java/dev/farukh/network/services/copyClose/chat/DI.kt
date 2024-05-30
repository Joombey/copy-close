package dev.farukh.network.services.copyClose.chat

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.Tags
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
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

val chatServiceDI by DI.Module {
    bindProvider(Tags.COPY_CLOSE_CHAT) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.COPY_CLOSE_CHAT) {
        HttpClient(OkHttp) {
            install(WebSockets) {
                maxFrameSize = 8192
                contentConverter = KotlinxWebsocketSerializationConverter(
                    instance(
                        Tags.COPY_CLOSE_CHAT
                    )
                )
            }
            install(ContentNegotiation) {
                json(instance(Tags.COPY_CLOSE_CHAT))
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 30000
            }
            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("BACK-COMMON", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url("${BuildConfig.CopyCloseURL}/chat/")
            }
        }
    }

    bindProvider<ChatService> {
        ChatServiceImpl(instance(Tags.COPY_CLOSE_CHAT))
    }
}