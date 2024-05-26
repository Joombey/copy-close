package dev.farukh.copyclose.features.order.list.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.creation.ui.compose.UserHeaderView
import dev.farukh.copyclose.features.order.list.data.dto.Service
import dev.farukh.copyclose.features.order.list.ui.OrderListActions
import dev.farukh.copyclose.features.order.list.ui.OrderListUIState
import dev.farukh.copyclose.features.order.list.ui.OrderUI

@Composable
fun OrderListView(
    uiState: OrderListUIState.OrderLoadedSate,
    actions: OrderListActions,
    modifier: Modifier = Modifier
) {
    val orderInfoOpened = uiState.orderInfoOpened
    if (orderInfoOpened != null) {
        OrderInfoDialog(
            comment = orderInfoOpened.comment,
            services = uiState.orderInfoOpened!!.serviceList,
            onDismissRequest = actions::dismissInfo,
            modifier = Modifier.padding(UiUtils.containerPaddingDefault)
        )
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
                if (orderUI.acceptable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
                    ) {
                        OutlinedButton(
                            onClick = { actions.accept(orderUI.orderID) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        ) {
                            Text(text = stringResource(R.string.accept))
                        }
                        OutlinedButton(
                            onClick = { actions.reject(orderUI.orderID) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            )
                        ) {
                            Text(text = stringResource(id = R.string.reject))
                        }
                    }
                }
            }
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
fun OrderInfoDialog(
    comment: String,
    services: List<Service>,
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