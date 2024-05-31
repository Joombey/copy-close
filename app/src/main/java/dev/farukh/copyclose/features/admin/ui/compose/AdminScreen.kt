package dev.farukh.copyclose.features.admin.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.core.ui.LoadingPopup
import dev.farukh.copyclose.core.utils.LoadingErrorButton
import dev.farukh.copyclose.features.admin.adminDI
import dev.farukh.copyclose.features.admin.ui.AdminViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier
) = withDI(di = adminDI(localDI())) {
    val viewModel: AdminViewModel by rememberViewModel()
    val uiState = viewModel.uiState

    Box(modifier = modifier) {
        when {
            uiState.error -> {
                LoadingErrorButton(onClick = viewModel::start)
            }

            uiState.loading -> {
                LoadingPopup(Modifier.align(Alignment.Center))
            }

            else -> {
                BlockListView(
                    blockList = uiState.blockList,
                    modifier = Modifier.fillMaxSize(),
                    onSetSolution = viewModel::sendSolution,
                    onDismissRequest = viewModel::dismiss,
                    onMoreInfoClick = viewModel::showInfo,
                    dialog = uiState.dialog
                )
            }
        }
    }
}