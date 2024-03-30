package dev.farukh.network.services.yandex.geoSuggester

import dev.farukh.network.services.yandex.geoSuggester.response.YandexGeoSuggestResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.path

interface YandexGeoSuggesterService {
    suspend fun query(query: String): RequestResult<YandexGeoSuggestResponse>
}

internal class YandexGeoSuggesterServiceImpl(
    private val client: HttpClient
) : YandexGeoSuggesterService {
    override suspend fun query(query: String) = client.commonGet(
        onResponse = {
            println(bodyAsText())
            body<YandexGeoSuggestResponse>()
        },
        config = {
            url {
                path("v1/suggest")
                parameters["text"] = query
                parameters["attrs"] = "uri"
            }
        }
    )
}