package dev.farukh.network.services

import dev.farukh.network.responses.AddressSuggestion
import dev.farukh.network.utils.RequestResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json

interface DaDataService {
    suspend fun getAddressSuggestion(query: String): RequestResult<List<AddressSuggestion>>
}

internal class DaDataServiceImpl(
    private val client: HttpClient,
    private val json: Json
) : DaDataService {
    override suspend fun getAddressSuggestion(
        query: String
    ) = client.postDaData(listOf("Булатниковская 3 2 23")) {
        body<List<AddressSuggestion>>()
    }


    private suspend inline fun <R> commonRequest(
        method: HttpMethod,
        onResponse: HttpResponse.() -> R,
        config: HttpRequestBuilder.() -> Unit,
    ): RequestResult<R> {
        val builder = HttpRequestBuilder().apply {
            this.method = method
            config()
        }

        return try {
            RequestResult.Success(onResponse(client.request(builder)))
        } catch (e: ClientRequestException) {
            return RequestResult.ClientError
        } catch (e: ServerResponseException) {
            return RequestResult.ServerInternalError
        }
    }

    private suspend inline fun <reified T, R> HttpClient.postDaData(
        body: T,
        onResponse: HttpResponse.() -> R
    ) = commonRequest(
        method = HttpMethod.Post,
        onResponse = onResponse,
        config = {
            url { path("api/v1/clean/address") }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    )
}