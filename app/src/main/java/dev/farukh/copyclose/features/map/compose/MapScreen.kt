package dev.farukh.copyclose.features.map.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dev.farukh.copyclose.features.map.mapDI
import org.kodein.di.compose.localDI
import org.kodein.di.compose.rememberViewModel
import org.kodein.di.compose.withDI
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun MapScreen(
    modifier: Modifier = Modifier
) = withDI(di = mapDI(localDI())) {
    val viewModel: MapViewModel by rememberViewModel()

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(7.0)
                    minZoomLevel = 4.0
                    maxZoomLevel = 16.0
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                }
            },
            modifier = Modifier.matchParentSize()
        )
    }
}