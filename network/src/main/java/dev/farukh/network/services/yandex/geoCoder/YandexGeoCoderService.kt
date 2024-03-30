package dev.farukh.network.services.yandex.geoCoder

import dev.farukh.network.services.yandex.geoCoder.response.GeoCoderResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.path

interface YandexGeoCoderService {
    suspend fun withQuery(query: String): RequestResult<GeoCoderResponse>
    suspend fun withUri(uri: String): RequestResult<GeoCoderResponse>
}

internal class YandexGeoCoderServiceImpl(
    private val client: HttpClient
): YandexGeoCoderService {
    override suspend fun withQuery(query: String): RequestResult<GeoCoderResponse> = client.commonGet(
        onResponse = { body() },
        config = {
            url {
                path("1.x/")
                parameters["geocode"] = query
                parameters["lang"] = "ru_RU"
                parameters["format"] = "json"
            }
        }
    )

    override suspend fun withUri(uri: String): RequestResult<GeoCoderResponse> = client.commonGet(
        onResponse = { body() },
        config = {
            url {
                path("1.x/")
                parameters["uri"] = uri
                parameters["lang"] = "ru_RU"
                parameters["format"] = "json"
            }
        }
    )
}