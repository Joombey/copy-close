package dev.farukh.copyclose.features.order.list.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.core.utils.LoadingErrorButton
import dev.farukh.copyclose.features.order.list.orderListDI
import dev.farukh.copyclose.features.order.list.ui.OrderListUIState
import dev.farukh.copyclose.features.order.list.ui.OrderListViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun OrderListScreen(modifier: Modifier = Modifier) = withDI(di = orderListDI(localDI())) {
    val viewModel: OrderListViewModel by rememberViewModel()
    val uiState = viewModel.state
    Box(modifier = modifier) {
        when (uiState) {
            is OrderListUIState.Error -> {
                LoadingErrorButton(onClick = viewModel::getOrders)
            }

            is OrderListUIState.Loading -> {
                LoadingPopup()
            }

            is OrderListUIState.OrderLoadedSate -> {
                OrderListView(
                    uiState = uiState,
                    actions = viewModel,
                    modifier = Modifier
                )
            }
        }
    }
}