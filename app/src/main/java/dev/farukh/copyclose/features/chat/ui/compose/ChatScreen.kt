package dev.farukh.copyclose.features.chat.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.core.utils.LoadingErrorButton
import dev.farukh.copyclose.features.chat.ChatVMDeps
import dev.farukh.copyclose.features.chat.chatDI
import dev.farukh.copyclose.features.chat.ui.ChatUIState
import dev.farukh.copyclose.features.chat.ui.ChatViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun ChatScreen(
    orderID: String,
    userId: String,
    modifier: Modifier = Modifier
) = withDI(chatDI(localDI())) {
    val viewModel: ChatViewModel by rememberViewModel(arg = ChatVMDeps(orderID, userId))
    val uiState = viewModel.state
    Box(modifier = modifier) {
        when (uiState) {
            is ChatUIState.Error -> {
                LoadingErrorButton(onClick = viewModel::connect)
            }

            is ChatUIState.Loading -> {
                LoadingPopup()
            }

            is ChatUIState.ChatMessages -> {
                ChatView(
                    uiState = uiState,
                    onSend = viewModel::sendMessage,
                    onTextChange = viewModel::setText,
                    modifier = modifier,
                )
            }
        }
    }
}