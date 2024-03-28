package dev.farukh.copyclose.features.register.ui.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import dev.farukh.copyclose.R
import dev.farukh.copyclose.core.model.Address
import dev.farukh.copyclose.features.register.ui.MapUIState
import dev.farukh.copyclose.utils.UiUtils
import dev.farukh.copyclose.utils.map.DrawUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapChooserView(
    uiState: MapUIState,
    onAddressClick: (Address) -> Unit,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val latestOnAddressClick by rememberUpdatedState(onAddressClick)
    var zoomInTrigger by remember { mutableStateOf(true) }
    var zoomOutTrigger by remember { mutableStateOf(true) }
    Log.i("map", "sate = ${(uiState.addressList as? SnapshotStateList)?.toList() == null}")
    Log.i("map", "sate = ${(uiState.addressList as? SnapshotStateList)?.toList()?.toString()}")
    Box(modifier) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    maxZoomLevel = 18.0
                    minZoomLevel = 3.0
                    controller.setZoom(3.0)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                    snapshotFlow {
                        (uiState.addressList as? SnapshotStateList)?.toList() ?: uiState.addressList
                    }
                        .onEach {
                            Log.i("map", "$it")
                        }
                        .onEach { setAddresses(it, latestOnAddressClick) }
                        .launchIn(scope)

                    snapshotFlow { zoomOutTrigger }
                        .onEach { controller.zoomOut() }
                        .launchIn(scope)

                    snapshotFlow { zoomInTrigger }
                        .onEach { controller.zoomIn() }
                        .launchIn(scope)
                }
            },
            modifier = Modifier.matchParentSize()
        )

        ZoomButtons(
            modifier = Modifier.align(Alignment.CenterEnd),
            onZoomIn = { zoomInTrigger = !zoomInTrigger },
            onZoomOut = { zoomOutTrigger = !zoomOutTrigger }
        )

        QueryField(
            query = uiState.query,
            addressList = uiState.addressList.toImmutableList(),
            onAddressClick = onAddressClick,
            onQueryChange = onQueryChange,
            modifier = Modifier
                .background(Color.Transparent)
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.3f)
        )
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
    query: String,
    addressList: ImmutableList<Address>,
    onAddressClick: (Address) -> Unit,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var queryFieldFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        AnimatedVisibility(visible = addressList.isEmpty()) {
            MapAddressListView(
                addressList = addressList,
                onAddress = onAddressClick
            )
        }
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusEvent ->
                    queryFieldFocused = focusEvent.isFocused
                }
        )
    }
}

private suspend fun MapView.setAddresses(
    addresses: List<Address>,
    onAddressClick: (Address) -> Unit
) {
    for (address in addresses) {
        val marker = Marker(this).apply {
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
    addresses.firstOrNull()?.let(onAddressClick)
}

private val Address.geoPoint get() = GeoPoint(lat, lon, 0.0)
