package dev.farukh.network.services.copyClose.chat.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ChatMessageResponse(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("text")
    val text: String,

    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val userName: String,
)