package dev.farukh.copyclose.features.chat.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.chat.data.repos.ChatRepository
import dev.farukh.copyclose.features.chat.domain.GetMessagesUseCase
import dev.farukh.network.services.copyClose.chat.response.ChatMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val orderID: String,
    private val userId: String,
    private val chatRepository: ChatRepository,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var _state by mutableStateOf<ChatUIState>(ChatUIState.Loading)
    val state: ChatUIState get() = _state

    init {
        connect()
    }


    fun connect() {
        getMessages()
        connectToSocket()
    }

    fun sendMessage() = viewModelScope.launch(Dispatchers.IO) {
        chatRepository.sendMessage(
            text = (state as? ChatUIState.ChatMessages)?.text ?: return@launch,
            userId = userId,
            orderId = orderID,
            authToken = userRepository.getActiveUser()!!.authToken!!
        )
        (_state as? ChatMessagesMutable)?.text = ""
    }

    fun setText(text: String) {
        (_state as? ChatMessagesMutable)?.text = text
    }

    private fun connectToSocket() = viewModelScope.launch {
        try {
            chatRepository.triggerFlow(orderID)
                .flowOn(Dispatchers.IO)
                .collect { getMessages() }
        } catch (_: Exception) {
            _state = ChatUIState.Error
        }
    }

    private fun getMessages() = viewModelScope.launch(Dispatchers.IO) {
        when (val messageResult = getMessagesUseCase(orderID, userId)) {
            is Result.Error -> _state = ChatUIState.Error
            is Result.Success -> {
                val uiMessages = messageResult.data.messages.toUI(
                    userID = userId,
                    iconMap = messageResult.data.userIconMap.mapValues { entry ->
                        withContext(Dispatchers.Default) {
                            UiUtils.bytesToImage(entry.value)!!
                        }
                    }
                )
                (_state as? ChatMessagesMutable)?.apply {
                    messagesMutable.addAll(
                        uiMessages.subList(
                            messagesMutable.size,
                            uiMessages.size
                        )
                    )
                } ?: run {
                    _state = ChatMessagesMutable(uiMessages)
                }
            }
        }
    }
}

sealed interface ChatUIState {
    data object Loading : ChatUIState
    data object Error : ChatUIState
    interface ChatMessages : ChatUIState {
        val messages: List<ChatMessageUI>
        val text: String
    }
}

private class ChatMessagesMutable(
    initialMessages: List<ChatMessageUI>
) : ChatUIState.ChatMessages {
    override var text: String by mutableStateOf("")

    val messagesMutable = initialMessages.toMutableStateList()
    override val messages: List<ChatMessageUI> get() = messagesMutable
}

data class ChatMessageUI(
    val id: String,
    val name: String,
    val icon: ImageBitmap,
    val mine: Boolean,
    val text: String
)

private suspend fun List<ChatMessageResponse>.toUI(
    userID: String,
    iconMap: Map<String, ImageBitmap>
) = map { response ->
    ChatMessageUI(
        name = response.userName,
        id = response.userId,
        mine = (userID == response.userId),
        text = response.text,
        icon = iconMap[response.userId]!!
    )
}