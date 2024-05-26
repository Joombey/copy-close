package dev.farukh.copyclose.features.order.creation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.core.utils.LoadingErrorButton
import dev.farukh.copyclose.features.order.creation.orderCreationDI
import dev.farukh.copyclose.features.order.creation.ui.OrderCreationUIState
import dev.farukh.copyclose.features.order.creation.ui.OrderCreationViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun OrderCreationScreen(
    sellerID: String,
    onProfileClick: () -> Unit,
    onOrderCreated: () -> Unit,
    modifier: Modifier = Modifier
) = withDI(di = orderCreationDI(localDI())) {
    val viewModel: OrderCreationViewModel by rememberViewModel(arg = sellerID)
    Box(modifier) {
        when (viewModel.uiState) {
            is OrderCreationUIState.Error -> {
                LoadingErrorButton(onClick = viewModel::getUserData)
            }

            is OrderCreationUIState.Loading -> {
                LoadingPopup()
            }

            is OrderCreationUIState.OrderCreationData -> {
                OrderCreationView(
                    uiState = viewModel.uiState as OrderCreationUIState.OrderCreationData,
                    actions = viewModel,
                    onProfileClick = onProfileClick,
                    onOrderCreated = onOrderCreated,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}