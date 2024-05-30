package dev.farukh.copyclose.features.chat

import dev.farukh.copyclose.features.chat.data.repos.ChatRepository
import dev.farukh.copyclose.features.chat.domain.GetMessagesUseCase
import dev.farukh.copyclose.features.chat.ui.ChatViewModel
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun chatDI(parentDI: DI) = DI {
    extend(parentDI)

    bindProvider { ChatRepository(instance()) }

    bindProvider {
        GetMessagesUseCase(
            chatRepository = instance(),
            userRepository = instance()
        )
    }

    bindFactory<ChatVMDeps, ChatViewModel> { deps ->
        ChatViewModel(
            userId = deps.userID,
            orderID = deps.orderID,
            userRepository = instance(),
            chatRepository = instance(),
            getMessagesUseCase = instance()
        )
    }
}

data class ChatVMDeps(
    val orderID: String,
    val userID: String,
)