package dev.farukh.network.di

import android.util.Log
import dev.farukh.network.BuildConfig
import dev.farukh.network.services.AuthService
import dev.farukh.network.services.AuthServiceImpl
import dev.farukh.network.services.DaDataService
import dev.farukh.network.services.DaDataServiceImpl
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

val networkDI = DI.Module("network") {
    import(daDataModule)
    import(copyCloseModule)
}

private val daDataModule = DI.Module("daData") {
    bindProvider(Tags.DA_DATA) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.DA_DATA) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(tag = Tags.DA_DATA))
            }
            install(DefaultRequest) {
                headers["Authorization"] = "Token ${BuildConfig.DaDataApiKey}"
                headers["X-Secret"] = BuildConfig.DaDataSecret
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

private val copyCloseModule = DI.Module("copyClose") {
    bindProvider(Tags.COPY_CLOSE) {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }
    }
    bindProvider(Tags.COPY_CLOSE) {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(instance(tag = Tags.COPY_CLOSE))
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