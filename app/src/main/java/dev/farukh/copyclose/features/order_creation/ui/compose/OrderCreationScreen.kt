package dev.farukh.copyclose.features.order_creation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.features.order_creation.orderCreationDI
import dev.farukh.copyclose.features.order_creation.ui.OrderCreationUIState
import dev.farukh.copyclose.features.order_creation.ui.OrderCreationViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun OrderCreationScreen(
    sellerID: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) = withDI(di = orderCreationDI(localDI())) {
    val viewModel: OrderCreationViewModel by rememberViewModel(arg = sellerID)
    Box(modifier) {
        when (viewModel.uiState) {
            is OrderCreationUIState.Error -> {
                Button(
                    onClick = viewModel::getUserData
                ) {
                    Text(text = stringResource(id = R.string.err_retry))
                }
            }

            is OrderCreationUIState.Loading -> {
                LoadingPopup()
            }

            is OrderCreationUIState.OrderCreationData -> {
                OrderCreationView(
                    uiState = viewModel.uiState as OrderCreationUIState.OrderCreationData,
                    actions = viewModel,
                    onProfileClick = onProfileClick,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}