package dev.farukh.network.services.copyClose.file

import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.ByteArrayOutputStream
import java.io.OutputStream

interface FileService {
    suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<Unit>
    suspend fun getImage(imageID: String): RequestResult<ByteArray>
}

internal class FileServiceImpl(private val client: HttpClient): FileService {
    override suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<Unit> = client.commonGet(
        onResponse = { bodyAsChannel().copyTo(dst) },
        config = { url("image/$imageID") }
    )

    override suspend fun getImage(imageID: String): RequestResult<ByteArray> {
        val dst = ByteArrayOutputStream()

        return when(val result = getImage(imageID, dst)) {
            is RequestResult.Success -> RequestResult.Success(dst.toByteArray())
            //else
            is RequestResult.ClientError -> result
            is RequestResult.HostError -> result
            is RequestResult.ServerError -> result
            is RequestResult.TimeoutError -> result
            is RequestResult.Unknown -> result
        }
    }
}