package dev.farukh.network.services.copyClose.authService

import dev.farukh.network.services.copyClose.authService.request.LogInRequest
import dev.farukh.network.services.copyClose.authService.request.RegisterRequest
import dev.farukh.network.services.copyClose.authService.response.RegisterResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonPost
import dev.farukh.network.utils.mimeString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface AuthService {
    suspend fun logIn(login: String, password: String): RequestResult<Boolean>
    suspend fun register(
        registerRequest: RegisterRequest,
        image: ByteArray
    ): RequestResult<RegisterResponse>
}

internal class AuthServiceImpl(
    private val json: Json,
    private val client: HttpClient
) : AuthService {

    override suspend fun logIn(login: String, password: String): RequestResult<Boolean> {
        return try {
            val result = client.post {
                contentType(ContentType.Application.Json)
                setBody(LogInRequest(login, password))
            }.status == HttpStatusCode.OK
            RequestResult.Success(result)
        } catch (e: ClientRequestException) {
            RequestResult.ClientError
        } catch (e: ServerResponseException) {
            RequestResult.ServerInternalError
        }
    }

    override suspend fun register(registerRequest: RegisterRequest, image: ByteArray) =
        client.commonPost(
            onResponse = { body<RegisterResponse>() },
            config = {
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "register",
                                value = json.encodeToString(registerRequest),
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        ContentType.Application.Json.mimeString
                                    )
                                }
                            )

                            append(
                                key = "image",
                                value = image,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        ContentType.Image.JPEG.mimeString
                                    )
                                }
                            )
                        }
                    )
                )
            }
        )
}