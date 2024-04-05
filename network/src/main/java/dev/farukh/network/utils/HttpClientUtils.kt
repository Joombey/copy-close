package dev.farukh.network.utils

import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import java.net.UnknownHostException

private suspend inline fun <R> HttpClient.commonRequest(
    method: HttpMethod,
    onResponse: HttpResponse.() -> R,
    config: HttpRequestBuilder.() -> Unit,
): RequestResult<R> {
    val builder = HttpRequestBuilder().apply {
        this.method = method
        config()
    }
    return try {
        RequestResult.Success(onResponse(request(builder)))
    } catch (e: ClientRequestException) {
        println(e)
        return RequestResult.ClientError(e.response.status.value, e.response.bodyAsText())
    } catch (e: ServerResponseException) {
        return RequestResult.ServerError
    } catch (e: SocketTimeoutException) {
        println(e)
        return RequestResult.TimeoutError
    } catch (e: UnknownHostException) {
        println(e)
        return RequestResult.HostError
    } catch (e: Exception) {
        println(e)
        return RequestResult.Unknown(e)
    }
}

internal suspend inline fun <R> HttpClient.commonGet(
    onResponse: HttpResponse.() -> R,
    config: HttpRequestBuilder.() -> Unit,
): RequestResult<R> = commonRequest(method = HttpMethod.Get, onResponse = onResponse, config = config)

internal suspend inline fun <R> HttpClient.commonPost(
    onResponse: HttpResponse.() -> R,
    config: HttpRequestBuilder.() -> Unit,
): RequestResult<R> = commonRequest(method = HttpMethod.Post, onResponse = onResponse, config = config)

internal suspend inline fun <R> HttpClient.commonPut(
    onResponse: HttpResponse.() -> R,
    config: HttpRequestBuilder.() -> Unit,
): RequestResult<R> = commonRequest(method = HttpMethod.Put, onResponse = onResponse, config = config)

internal val ContentType.mimeString get() = "$contentType/$contentSubtype"
