package dev.farukh.network.services.copyClose.file

import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.OutputStream
import java.nio.ByteBuffer

interface FileService {
    suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<ByteArray>
}

internal class FileServiceImpl(private val client: HttpClient): FileService {
    override suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<ByteArray> = client.commonGet(
        onResponse = {
            val byteBuffer = ByteBuffer.allocate(contentLength()!!.toInt())
            bodyAsChannel().copyTo(dst)
            byteBuffer.array()
        },
        config = {
            url("image/$imageID")
        }
    )
}