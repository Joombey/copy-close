package dev.farukh.network.services.copyClose.file

import dev.farukh.network.core.UploadProgress
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import dev.farukh.network.utils.commonPost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface FileService {
    suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<Unit>
    suspend fun getImage(imageID: String): RequestResult<ByteArray>
    suspend fun createUploadSession(filename: String, fileSize: Long): RequestResult<String>
    suspend fun sendFile(
        file: File,
        sessionId: String
    ): Flow<RequestResult<UploadProgress<String, String?>>>

    suspend fun sendFile(
        name: String,
        streamLauncher: () -> InputStream,
        size: Long,
        sessionID: String
    ): Flow<RequestResult<UploadProgress<String, String?>>>
}

internal class FileServiceImpl(private val client: HttpClient) : FileService {
    override suspend fun getImage(imageID: String, dst: OutputStream): RequestResult<Unit> =
        client.commonGet(
            onResponse = { bodyAsChannel().copyTo(dst) },
            config = { url("image/$imageID") }
        )

    override suspend fun getImage(imageID: String): RequestResult<ByteArray> {
        val dst = ByteArrayOutputStream()

        return when (val result = getImage(imageID, dst)) {
            is RequestResult.Success -> RequestResult.Success(dst.toByteArray())

            is RequestResult.ClientError -> result
            is RequestResult.HostError -> result
            is RequestResult.ServerError -> result
            is RequestResult.TimeoutError -> result
            is RequestResult.Unknown -> result
        }
    }

    override suspend fun createUploadSession(
        filename: String,
        fileSize: Long
    ): RequestResult<String> = client.commonGet(
        onResponse = { bodyAsText() },
        config = {
            url("create-session")
            url {
                parameters["filename"] = filename
                parameters["length"] = fileSize.toString()
            }
        }
    )


    override suspend fun sendFile(file: File, sessionId: String) = if (!file.exists()) flow {
        emit(
            RequestResult.ClientError(
                400,
                "file doesn't exists"
            )
        )
    } else sendFile(
        name = file.name,
        streamLauncher = { file.inputStream() },
        size = file.length(),
        sessionID = sessionId
    )


    override suspend fun sendFile(
        name: String,
        streamLauncher: () -> InputStream,
        size: Long,
        sessionID: String
    ) = flow {
        streamLauncher().use { stream ->
            val buffer = ByteArray(FILE_LEN_THRESHOLD)
            var totalReadCounter = 0L
            while (true) {
                val readCount = stream.read(buffer, 0, buffer.size)
                if (readCount == -1) break
                totalReadCounter += readCount
                val chunkResult = sendChunkOfDataToSession(
                    chunk = buffer.sliceArray(0 until readCount),
                    filename = name,
                    sessionID = sessionID
                )
                emit(
                    when (chunkResult) {
                        is RequestResult.Success -> {
                            RequestResult.Success(
                                UploadProgress(
                                    id = name,
                                    sent = totalReadCounter,
                                    total = size,
                                    data = chunkResult.data.ifEmpty { null }
                                )
                            )
                        }

                        is RequestResult.ClientError -> chunkResult
                        is RequestResult.HostError -> chunkResult
                        is RequestResult.ServerError -> chunkResult
                        is RequestResult.TimeoutError -> chunkResult
                        is RequestResult.Unknown -> chunkResult
                    }
                )
            }
        }
    }
        .flowOn(Dispatchers.IO)

    private suspend fun sendChunkOfDataToSession(
        chunk: ByteArray,
        filename: String,
        sessionID: String
    ) = client.commonPost(
        onResponse = { bodyAsText() },
        config = {
            url("upload/$sessionID")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "data",
                            value = chunk,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"$filename\""
                                )
                            }
                        )
                    }
                )
            )
        }
    )

    internal companion object {
        const val FILE_LEN_THRESHOLD = 8 * 1024 * 1024
    }
}