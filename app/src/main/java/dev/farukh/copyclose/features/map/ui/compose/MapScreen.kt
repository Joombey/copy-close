package dev.farukh.copyclose.features.map.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.farukh.copyclose.features.map.mapDI
import dev.farukh.copyclose.features.map.ui.MapViewModel
import dev.farukh.copyclose.features.map.ui.model.SellerUI
import dev.farukh.copyclose.features.register.ui.compose.ZoomButtons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun MapScreen(
    onSellerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) = withDI(di = mapDI(localDI())) {
    val viewModel: MapViewModel by rememberViewModel()

    var zoomInTrigger: Boolean? by remember { mutableStateOf(null) }
    var zoomOutTrigger: Boolean? by remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.getSellers() }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    controller.setZoom(viewModel.mapUIState.zoomLevel)
                    controller.setCenter(viewModel.mapUIState.mapCenter)

                    minZoomLevel = 4.0
                    maxZoomLevel = 16.0
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                    setZoomInTrigger(snapshotFlow { zoomInTrigger }, scope)
                    setZoomOutTrigger(snapshotFlow { zoomOutTrigger }, scope)
                    collectSellers(
                        sellersFlow = viewModel.sellersFlow,
                        scope = scope,
                        onClick = { onSellerClick(it.id) },
                        onLongClick = { viewModel.setSellerLocation(it) }
                    )
                }
            },

            onRelease = {
                viewModel.setCenter(it.mapCenter)
                viewModel.setZoom(it.zoomLevelDouble)
            },
            modifier = Modifier.matchParentSize()
        )

        ZoomButtons(
            onZoomIn = { zoomInTrigger = zoomInTrigger?.not() ?: false },
            onZoomOut = { zoomOutTrigger = zoomOutTrigger?.not() ?: false },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )
    }
}

private fun MapView.collectSellers(
    sellersFlow: Flow<List<SellerUI>>,
    scope: CoroutineScope,
    onLongClick: (SellerUI) -> Unit,
    onClick: (SellerUI) -> Unit,
) {
    sellersFlow.onEach { sellers ->
        removeAllViews()
        sellers.forEach { seller ->
            addView(
                markerView(
                    sellerUI = seller,
                    context = context,
                    onLongClick = { onLongClick(seller) },
                    onClick = { onClick(seller) }
                )
            )
        }
    }.launchIn(scope)
}

private fun MapView.setZoomInTrigger(zoomInTrigger: Flow<Boolean?>, scope: CoroutineScope) {
    zoomInTrigger
        .filterNotNull()
        .onEach { controller.zoomIn() }
        .launchIn(scope)
}

private fun MapView.setZoomOutTrigger(zoomOutTrigger: Flow<Boolean?>, scope: CoroutineScope) {
    zoomOutTrigger
        .filterNotNull()
        .onEach { controller.zoomOut() }
        .launchIn(scope)
}