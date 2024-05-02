package dev.farukh.copyclose.features.register.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.register.data.model.Address
import dev.farukh.copyclose.features.register.ui.QueryUIState
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ZoomButtons(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault)
    ) {
        IconButton(
            onClick = onZoomIn
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }

        IconButton(
            onClick = onZoomOut
        ) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
        }
    }
}

@Composable
fun QueryField(
    uiState: QueryUIState,
    onAddressClick: (Address) -> Unit,
    onQueryChange: (String) -> Unit,
    onQueryClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiUtils.arrangementDefault),
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = onQueryClick,
                ) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                }
            },
            label = label,
            singleLine = true,
        )

        AnimatedVisibility(uiState.addressListShown) {
            MapAddressListView(
                addressList = uiState.addressList.toImmutableList(),
                onAddress = onAddressClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = UiUtils.borderWidthDefault,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = UiUtils.roundShapeDefault
                    )
            )
        }
    }
}
