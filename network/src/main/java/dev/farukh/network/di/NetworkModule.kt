package dev.farukh.network.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dev.farukh.network.utils.DaDataInterceptor
import dev.farukh.network.services.DaDataService
import dev.farukh.network.services.DaDataServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Module
class NetworkModule {

    @Provides
    fun provideJson() = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    fun provideHttpClient(
        json: Json
    ) = HttpClient(OkHttp) {
        engine {
            addInterceptor(DaDataInterceptor())
        }
        install(ContentNegotiation) {
            json(json)
        }
        Logging {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.i("ktor", message)
                }
            }
        }
    }

    @Provides
    fun provideDaDataService(
        client: HttpClient,
        json: Json
    ): DaDataService = DaDataServiceImpl(client, json)
}