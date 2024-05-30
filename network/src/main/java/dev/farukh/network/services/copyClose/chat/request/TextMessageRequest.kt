package dev.farukh.network.services.copyClose.chat.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class
TextMessageRequest(
    @SerialName("text")
    val text: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("auth_token")
    val authToken: String,
)