package dev.farukh.copyclose.features.admin.ui.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.admin.ui.BlockItemUI
import dev.farukh.copyclose.features.order.creation.ui.compose.UserHeaderView
import dev.farukh.copyclose.features.order.list.ui.compose.OrderInfoDialog
import dev.farukh.copyclose.features.order.list.ui.compose.TotalPriceWithInfoView

@Composable
fun BlockListView(
    blockList: List<BlockItemUI>,
    onDismissRequest: () -> Unit,
    onMoreInfoClick: (BlockItemUI) -> Unit,
    onSetSolution: (BlockItemUI, Boolean) -> Unit,
    dialog: BlockItemUI?,
    modifier: Modifier = Modifier
) {
    dialog?.let {
        OrderInfoDialog(
            comment = dialog.comment,
            attachment = emptyList(),
            services = dialog.services,
            onDismissRequest = onDismissRequest,
            canDownload = false,
            onDownload = {},
            modifier = Modifier.padding(UiUtils.containerPaddingDefault)
        )
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault / 2),
        contentPadding = UiUtils.contentPaddingDefault
    ) {
        items(
            items = blockList,
            key = { it.orderID },
        ) { blockItemUI ->
            BlockItemView(
                blockItemUI = blockItemUI,
                onMoreInfoClick = { onMoreInfoClick(blockItemUI) },
                onBlock = { onSetSolution(blockItemUI, true) },
                onDecline = { onSetSolution(blockItemUI, false) },
                modifier = Modifier
                    .clip(UiUtils.roundShapeDefault)
                    .border(
                        width = UiUtils.borderWidthDefault,
                        color = MaterialTheme.colorScheme.outline,
                        shape = UiUtils.roundShapeDefault
                    )
                    .fillParentMaxWidth()
                    .padding(UiUtils.containerPaddingDefault)
            )
        }
    }
}

@Composable
fun BlockItemView(
    blockItemUI: BlockItemUI,
    onMoreInfoClick: () -> Unit,
    onDecline: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault / 2)
    ) {
        UserHeaderView(
            userIcon = blockItemUI.icon,
            userName = blockItemUI.userName,
            userAddress = blockItemUI.address.addressName,
            onIconClick = { }
        )
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
        ) {
            Text(stringResource(id = R.string.date, blockItemUI.date))
            TotalPriceWithInfoView(
                totalPrice = blockItemUI.priceTotal,
                onMoreInfoClick = onMoreInfoClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (blockItemUI.reportMessage.isNotEmpty()) {
            Text(text = stringResource(R.string.report_message_label))
            HorizontalDivider(color = Color.Black)
            Text(
                text = blockItemUI.reportMessage,
                modifier = Modifier
                    .border(
                        width = UiUtils.borderWidthDefault / 4,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RectangleShape
                    )
                    .fillMaxWidth()
                    .padding(UiUtils.borderWidthDefault / 4 + 5.dp),
                maxLines = 10,

            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault / 4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBlock,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.block))
            }
            OutlinedButton(
                onClick = onDecline,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.decline))
            }
        }
    }
}