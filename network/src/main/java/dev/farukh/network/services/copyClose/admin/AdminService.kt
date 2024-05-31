package dev.farukh.network.services.copyClose.admin

import dev.farukh.network.services.copyClose.admin.request.BlockRequest
import dev.farukh.network.services.copyClose.admin.response.BlockResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import dev.farukh.network.utils.commonPut
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdminService {
    suspend fun getBlockList(id: String, authToken: String): RequestResult<List<BlockResponse>>
    suspend fun block(request: BlockRequest, block: Boolean): RequestResult<Unit>

    fun getUpdates(
        id: String,
        authToken: String
    ): Flow<Unit>
}

internal class AdminServiceImpl(
    private val client: HttpClient,
) : AdminService {

    override suspend fun block(request: BlockRequest, block: Boolean): RequestResult<Unit> {
        return client.commonPut(
            onResponse = {},
            config = {
                url {
                    url("block")
                    parameters["block"] = block.toString()
                }
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        )
    }

    override suspend fun getBlockList(
        id: String,
        authToken: String
    ): RequestResult<List<BlockResponse>> = client.commonGet(
        onResponse = {
            body<List<BlockResponse>>()
        },
        config = {
            url {
                url("blocklist")
                parameters["user_id"] = id
                parameters["auth_token"] = authToken
            }
        }
    )

    override fun getUpdates(
        id: String,
        authToken: String
    ): Flow<Unit> = flow {
        client.webSocket(
            request = {
                url {
                    url("blocklist")
                    parameters["user_id"] = id
                    parameters["auth_token"] = authToken
                    parameters["listen"] = "true"
                }
            }
        ) {
            for (frame in incoming) {
                emit(Unit)
            }
        }
    }
}