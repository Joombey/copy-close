package dev.farukh.copyclose.features.chat.ui.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.core.utils.CircleImage
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.chat.ui.ChatMessageUI
import dev.farukh.copyclose.features.chat.ui.ChatUIState

@Composable
fun ChatView(
    uiState: ChatUIState.ChatMessages,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = uiState.messages.size) {
        listState.animateScrollToItem(uiState.messages.lastIndex)
    }

    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
        ) {
            itemsIndexed(
                items = uiState.messages,
                key = { index, _ -> index }
            ) { _, chatMessage ->
                UserTextMessage(
                    chatMessage = chatMessage,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
        OutlinedTextField(
            value = uiState.text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = onSend) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
fun UserTextMessage(
    chatMessage: ChatMessageUI,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        if (chatMessage.mine) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.weight(3f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = chatMessage.text,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = UiUtils.borderWidthDefault / 2,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(10.dp)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.padding(UiUtils.arrangementDefault / 4))
                UserChatIcon(icon = chatMessage.icon, name = chatMessage.name)
            }
        } else {
            Row(
                modifier = Modifier.weight(3f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                UserChatIcon(icon = chatMessage.icon, name = chatMessage.name)
                Spacer(modifier = Modifier.padding(UiUtils.arrangementDefault / 4))
                Text(
                    text = chatMessage.text,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = UiUtils.borderWidthDefault / 2,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(10.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun UserChatIcon(
    icon: ImageBitmap,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault / 8),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleImage(
            icon = icon,
            size = UiUtils.imageSizeMedium,
            modifier = Modifier
                .border(
                    width = UiUtils.borderWidthDefault / 2,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        )
        Text(text = name, style = MaterialTheme.typography.bodySmall)
    }
}