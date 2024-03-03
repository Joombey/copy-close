package dev.farukh.network.services

import dev.farukh.network.responses.AddressSuggestion
import dev.farukh.network.utils.RequestResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json

interface DaDataService {
    suspend fun getAddressSuggestion(query: String): RequestResult<List<AddressSuggestion>>
}

internal class DaDataServiceImpl(
    private val client: HttpClient,
    private val json: Json
) : DaDataService {
    private val secretKey = ""
    private val apiKey = ""

    override suspend fun getAddressSuggestion(
        query: String
    ) = client.postDaData(listOf(query)) {
        body<List<AddressSuggestion>>()
    }


    private suspend inline fun <R> commonRequest(
        method: HttpMethod,
        onResponse: HttpResponse.() -> R
    ): RequestResult<R> {
        val builder = HttpRequestBuilder().apply {
            this.method = method
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
        onResponse = onResponse
    )
}