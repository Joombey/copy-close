package dev.farukh.copyclose.features.register.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.core.utils.map.DrawUtils
import dev.farukh.copyclose.features.register.data.model.Address
import dev.farukh.copyclose.features.register.ui.QueryUIState
import dev.farukh.copyclose.features.register.ui.map.AddressMarker
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun MapChooserView(
    uiState: QueryUIState,
    onAddressClick: (Address) -> Unit,
    onQueryChange: (String) -> Unit,
    onQueryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val latestOnAddressClick by rememberUpdatedState(onAddressClick)
    var zoomInTrigger by remember { mutableStateOf(true) }
    var zoomOutTrigger by remember { mutableStateOf(true) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier = modifier.onGloballyPositioned { boxSize = it.size }) {
//        AndroidView(
//            factory = { context ->
//                AddressChooseMap(context).apply {
//                    clipBounds = Rect(0, 0, boxSize.width, boxSize.height)
//                    snapshotFlow {
//                        (uiState.addressList as? SnapshotStateList)?.toList() ?: uiState.addressList
//                    }
//                        .onEach { setAddresses(it, latestOnAddressClick) }
//                        .onEach { Log.i("map", it.size.toString()) }
//                        .launchIn(scope)
//
//                    snapshotFlow { zoomOutTrigger }
//                        .onEach { controller.zoomOut() }
//                        .launchIn(scope)
//
//                    snapshotFlow { zoomInTrigger }
//                        .onEach { controller.zoomIn() }
//                        .launchIn(scope)
//                }
//            },
//            modifier = Modifier.matchParentSize()
//        )

        ZoomButtons(
            modifier = Modifier.align(Alignment.CenterEnd),
            onZoomIn = { zoomInTrigger = !zoomInTrigger },
            onZoomOut = { zoomOutTrigger = !zoomOutTrigger }
        )

//        QueryField(
//            query = uiState.query,
//            addressList = uiState.addressList.toImmutableList(),
//            onAddressClick = onAddressClick,
//            onQueryChange = onQueryChange,
//            onQueryClick = onQueryClick,
//            modifier = Modifier
//                .background(Color.Transparent)
//                .align(Alignment.BottomCenter)
//                .fillMaxHeight(0.3f)
//        )
    }
}

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

private suspend fun MapView.setAddresses(
    addresses: List<Address>,
    onAddressClick: (Address) -> Unit
) {
    overlays.removeAll { it is AddressMarker }
    for (address in addresses) {
        val marker = AddressMarker(this).apply {
            position = address.geoPoint
            icon = withContext(Dispatchers.Default) {
                DrawUtils.bitmapFromResource(resources, R.drawable.ic_marker)
            }
            setOnMarkerClickListener { _, _ ->
                controller.animateTo(position)
                onAddressClick(address)
                true
            }
        }
        overlays += marker
        invalidate()
    }
}

private val Address.geoPoint get() = GeoPoint(lat, lon, 0.0)
