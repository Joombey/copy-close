package dev.farukh.network.services.copyClose.common

import dev.farukh.network.services.copyClose.common.response.UserInfoResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.path
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.nio.ByteBuffer

class CommonService(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getUserInfo(login: String, authToken: String) = client.commonGet(
        onResponse = { body<UserInfoResponse>() },
        config = {
            url {
                path("/common/getUserInfo/$login")
                parameters["authToken"] = authToken
            }
        }
    )

    suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<ByteArray> = client.commonGet(
        onResponse = {
            val byteBuffer = ByteBuffer.allocate(contentLength()!!.toInt())
            bodyAsChannel().copyTo(dst)
            byteBuffer.array()
        },
        config = { url { path("/common/getImage/$imageID") } }
    )
}