package dev.farukh.network.services.copyClose.auth

import dev.farukh.network.services.copyClose.auth.request.LogInRequest
import dev.farukh.network.services.copyClose.auth.request.RegisterRequest
import dev.farukh.network.services.copyClose.auth.response.RegisterResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonPost
import dev.farukh.network.utils.mimeString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface AuthService {
    suspend fun logIn(login: String, password: String): RequestResult<String>
    suspend fun register(
        registerRequest: RegisterRequest,
        image: ByteArray,
        devKey: String?,
    ): RequestResult<RegisterResponse>
}

internal class AuthServiceImpl(
    private val json: Json,
    private val client: HttpClient
) : AuthService {

    override suspend fun logIn(login: String, password: String): RequestResult<String> =
        client.commonPost(
            onResponse = { bodyAsText() },
            config = {
                url("login")
                contentType(ContentType.Application.Json)
                setBody(LogInRequest(login, password))
            }
        )

    override suspend fun register(
        registerRequest: RegisterRequest,
        image: ByteArray,
        devKey: String?,
    ) = client.commonPost(
        onResponse = { body<RegisterResponse>() },
        config = {
            url {
                url("register")
                if (devKey != null) {
                    parameters["devKey"] = devKey
                }
            }
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
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=1"
                                )
                            }
                        )
                    }
                )
            )
        }
    )
}