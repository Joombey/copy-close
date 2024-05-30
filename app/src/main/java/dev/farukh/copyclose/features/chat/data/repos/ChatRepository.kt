package dev.farukh.copyclose.features.chat.data.repos

import dev.farukh.copyclose.core.utils.extensions.asResult
import dev.farukh.network.services.copyClose.chat.ChatService
import dev.farukh.network.services.copyClose.chat.request.TextMessageRequest

class ChatRepository(
    private val chatService: ChatService
) {
    suspend fun sendMessage(
        text: String,
        userId: String,
        orderId: String,
        authToken: String,
    ) = chatService.sendMessage(
        orderID = orderId,
        textMessageRequest = TextMessageRequest(
            text = text,
            userId = userId,
            authToken = authToken,
        )
    ).asResult()


    suspend fun getMessages(orderId: String) = chatService.getMessages(orderID = orderId).asResult()

    fun triggerFlow(orderId: String) = chatService.getChatTrigger(orderId)
}