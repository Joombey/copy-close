package dev.farukh.copyclose.features.chat.data.dto

import dev.farukh.network.services.copyClose.chat.response.ChatMessageResponse

class ChatDTO(
    val userIconMap: Map<String, ByteArray>,
    val messages: List<ChatMessageResponse>
)