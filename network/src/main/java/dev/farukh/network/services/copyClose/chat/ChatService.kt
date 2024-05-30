package dev.farukh.network.services.copyClose.chat

import dev.farukh.network.services.copyClose.chat.request.TextMessageRequest
import dev.farukh.network.services.copyClose.chat.response.ChatMessageResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import dev.farukh.network.utils.commonPost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ChatService {
    suspend fun sendMessage(
        orderID: String,
        textMessageRequest: TextMessageRequest
    ): RequestResult<Unit>

    suspend fun getMessages(orderID: String): RequestResult<List<ChatMessageResponse>>

    fun getChatTrigger(orderID: String): Flow<Unit>
}

internal class ChatServiceImpl(
    private val client: HttpClient
) : ChatService {
    override suspend fun getMessages(orderID: String): RequestResult<List<ChatMessageResponse>> =
        client.commonGet(
            onResponse = { body<List<ChatMessageResponse>>() },
            config = { url("messages/$orderID") }
        )

    override suspend fun sendMessage(
        orderID: String,
        textMessageRequest: TextMessageRequest
    ): RequestResult<Unit> =
        client.commonPost(
            onResponse = {},
            config = {
                contentType(ContentType.Application.Json)
                setBody(textMessageRequest)
                url("messages/$orderID")
            }
        )

    override fun getChatTrigger(orderID: String): Flow<Unit> = flow {
        client.webSocket(
            request = {
                url {
                    url("messages/$orderID")
                    parameters["chat"] = "true"
                }
            }
        ) {
            for (frame in incoming) {
                emit(Unit)
            }
        }
    }
}