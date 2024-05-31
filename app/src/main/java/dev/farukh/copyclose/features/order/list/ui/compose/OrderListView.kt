package dev.farukh.copyclose.features.order.list.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.data.models.Service
import dev.farukh.copyclose.core.ui.InfoDialog
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.creation.ui.compose.AttachedFile
import dev.farukh.copyclose.features.order.creation.ui.compose.UserHeaderView
import dev.farukh.copyclose.features.order.list.data.dto.Attachment
import dev.farukh.copyclose.features.order.list.data.dto.OrderState
import dev.farukh.copyclose.features.order.list.ui.OrderListActions
import dev.farukh.copyclose.features.order.list.ui.OrderListDialogState
import dev.farukh.copyclose.features.order.list.ui.OrderListUIState
import dev.farukh.copyclose.features.order.list.ui.OrderUI

@Composable
fun OrderListView(
    uiState: OrderListUIState.OrderLoadedSate,
    actions: OrderListActions,
    onChat: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dialogState = uiState.dialogState
    val context = LocalContext.current
    when (dialogState) {
        OrderListDialogState.None -> {
        }

        is OrderListDialogState.OrderInfo -> {
            val orderInfoOpened = dialogState.orderUI
            OrderInfoDialog(
                comment = orderInfoOpened.comment,
                attachment = orderInfoOpened.attachments,
                services = orderInfoOpened.serviceList,
                onDismissRequest = actions::dismissDialog,
                canDownload = orderInfoOpened.acceptable && orderInfoOpened.state == OrderState.STATE_ACCEPTED,
                onDownload = { downloadUri ->
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            downloadUri
                        )
                    )
                },
                modifier = Modifier.padding(UiUtils.containerPaddingDefault)
            )
        }

        is OrderListDialogState.Reporting -> {
            ReportDialog(
                text = dialogState.message,
                onTextChange = actions::setDialogMessage,
                onReport = actions::report,
                onDismissRequest = actions::dismissDialog
            )
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
        contentPadding = UiUtils.contentPaddingDefault,
    ) {
        items(
            items = uiState.orders,
            key = { it.orderID }
        ) { orderUI ->
            Column(
                modifier = Modifier
                    .clip(UiUtils.roundShapeDefault)
                    .border(
                        width = UiUtils.borderWidthDefault,
                        color = MaterialTheme.colorScheme.outline,
                        shape = UiUtils.roundShapeDefault
                    )
                    .fillParentMaxWidth()
                    .padding(UiUtils.containerPaddingDefault)
            ) {
                OrderItem(
                    orderUI = orderUI,
                    onMoreInfoClick = actions::info,
                    modifier = Modifier
                        .animateContentSize()
                        .fillMaxWidth()
                )
                when (orderUI.state) {
                    OrderState.STATE_REQUESTED -> {
                        if (orderUI.acceptable)
                            AcceptRejectView(
                                onAccept = { actions.accept(orderUI.orderID) },
                                onReject = { actions.reject(orderUI.orderID) },
                                modifier = Modifier.fillMaxWidth()
                            )
                    }

                    OrderState.STATE_ACCEPTED -> {
                        if (!orderUI.acceptable) {
                            ButtonWithChat(
                                text = stringResource(id = R.string.accepted),
                                onChat = { onChat(orderUI.orderID) }
                            )
                        } else {
                            ButtonWithChat(
                                text = stringResource(id = R.string.end_order),
                                onChat = { onChat(orderUI.orderID) },
                                onClick = { actions.finish(orderUI.orderID) }
                            )
                        }
                    }

                    OrderState.STATE_REJECTED -> {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            )
                        ) {
                            Text(text = stringResource(id = R.string.rejected))
                        }
                    }

                    OrderState.STATE_COMPLETED -> {
                        OutlinedButton(
                            onClick = {
                                if (!orderUI.acceptable && !orderUI.reported) {
                                    actions.openReport(orderUI)
                                }
                            },
                            enabled = !orderUI.acceptable && !orderUI.reported,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        ) {
                            Text(
                                text = if (orderUI.acceptable || orderUI.reported) {
                                    stringResource(R.string.completed)
                                } else {
                                    stringResource(id = R.string.report)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonWithChat(
    text: String,
    onChat: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        OutlinedButton(
            onClick = { onClick?.invoke() },
            modifier = Modifier.weight(1f),
            enabled = onClick != null,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = text)
        }
        IconButton(onClick = onChat) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
            )
        }
    }

}

@Composable
fun AcceptRejectView(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        OutlinedButton(
            onClick = onAccept,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Text(text = stringResource(R.string.accept))
        }
        OutlinedButton(
            onClick = onReject,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            )
        ) {
            Text(text = stringResource(id = R.string.reject))
        }
    }
}

@Composable
fun OrderItem(
    orderUI: OrderUI,
    onMoreInfoClick: (OrderUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        UserHeaderView(
            userIcon = orderUI.icon!!,
            userName = orderUI.name,
            userAddress = orderUI.addressName,
            onIconClick = { },
            modifier = Modifier,
        )
        TotalPriceWithInfoView(
            totalPrice = orderUI.totalPrice,
            onMoreInfoClick = { onMoreInfoClick(orderUI) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TotalPriceWithInfoView(
    totalPrice: Int,
    onMoreInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(id = R.string.total), Modifier.weight(1f))
        Text(stringResource(id = R.string.price, totalPrice))
        IconButton(onClick = onMoreInfoClick) {
            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
        }
    }
}

@Composable
fun ReportDialog(
    text: String,
    onTextChange: (String) -> Unit,
    onReport: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    InfoDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = DialogProperties(),
    ) {
        Text(
            text = stringResource(id = R.string.report_label),
            style = MaterialTheme.typography.labelLarge
        )
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = onReport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.report))
        }
    }
}

@Composable
fun OrderInfoDialog(
    comment: String,
    attachment: List<Attachment>,
    services: List<Service>,
    canDownload: Boolean,
    onDownload: (Uri) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .then(modifier)
        ) {
            if (comment.isNotEmpty()) {
                Text(stringResource(R.string.comment_label))
                Text(
                    text = comment,
                    maxLines = 10,
                    modifier = Modifier
                        .border(
                            width = UiUtils.borderWidthDefault / 2,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RectangleShape
                        )
                        .padding(UiUtils.borderWidthDefault)
                        .fillMaxWidth()
                )
            }
            if (attachment.isNotEmpty()) {
                Text(stringResource(R.string.attachment_label))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = UiUtils.dividerThicknessDefault,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                AttachmentsView(
                    attachedFiles = attachment,
                    content = { view, att ->
                        view()
                        if (canDownload) {
                            IconButton(
                                onClick = { onDownload(att.url.toUri()) }
                            ) {
                                Icon(Icons.Filled.Download, null)
                            }
                        }
                    },
                )
            }
            Row {
                Text(stringResource(R.string.service_profile_title))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.amount_title))
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = UiUtils.dividerThicknessDefault,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            LazyColumn {
                itemsIndexed(
                    items = services,
                    key = { index, _ -> index }
                ) { _, service ->
                    Row(
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        Text(service.title)
                        Spacer(Modifier.weight(1f))
                        Text(
                            stringResource(
                                id = R.string.price_amount_format,
                                service.amount,
                                service.price
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentsView(
    attachedFiles: List<Attachment>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(@Composable () -> Unit, Attachment) -> Unit,
) {
    var containerSize by remember {
        mutableStateOf(Size.Zero)
    }
    LazyRow(
        modifier = modifier.onGloballyPositioned { containerSize = it.size.toSize() },
        horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
        contentPadding = UiUtils.contentPaddingDefault
    ) {
        itemsIndexed(
            items = attachedFiles,
            key = { index, _ -> index }
        ) { _, item ->
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .padding(UiUtils.containerPaddingDefault),
                verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content(
                    {
                        AttachedFile(
                            extension = item.name.split(".").last(),
                            name = item.name.split(".").first(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(UiUtils.containerPaddingDefault)
                        )
                    },
                    item,
                )
            }
        }
    }
}