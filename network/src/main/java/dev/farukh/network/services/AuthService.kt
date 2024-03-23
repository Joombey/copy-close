package dev.farukh.network.services

import dev.farukh.network.requests.SignInModel
import dev.farukh.network.requests.SignUpModel
import dev.farukh.network.utils.RequestResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

interface AuthService {
    suspend fun signIn(login: String, password: String): RequestResult<Boolean>
    suspend fun signUp(
        login: String,
        password: String,
        name: String,
        address: String,
        lat: Double,
        lon: Double,
    ): RequestResult<Boolean>
}

class AuthServiceImpl(
    private val json: Json,
    private val client: HttpClient
) : AuthService {

    override suspend fun signIn(login: String, password: String): RequestResult<Boolean> {
        return try {
            val result = client.post {
                contentType(ContentType.Application.Json)
                setBody(SignInModel(login, password))
            }.status == HttpStatusCode.OK
            RequestResult.Success(result)
        } catch (e: ClientRequestException) {
            RequestResult.ClientError
        } catch (e: ServerResponseException) {
            RequestResult.ServerInternalError
        }
    }

    override suspend fun signUp(
        login: String,
        password: String,
        name: String,
        address: String,
        lat: Double,
        lon: Double
    ): RequestResult<Boolean> {
        return try {
            val result = client.post {
                contentType(ContentType.Application.Json)
                setBody(
                    SignUpModel(
                        login = login,
                        password = password,
                        name = name ,
                        address = address,
                        lat = lat,
                        lon = lon
                    )
                )
            }.status == HttpStatusCode.OK
            RequestResult.Success(result)
        } catch (e: ClientRequestException) {
            RequestResult.ClientError
        } catch (e: ServerResponseException) {
            RequestResult.ServerInternalError
        }
    }
}