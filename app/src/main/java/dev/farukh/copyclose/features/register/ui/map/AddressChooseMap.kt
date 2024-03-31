package dev.farukh.copyclose.features.register.ui.map

import android.content.Context
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class AddressChooseMap(context: Context): MapView(context) {
    init {
        setTileSource(TileSourceFactory.MAPNIK)
        maxZoomLevel = 18.0
        minZoomLevel = 3.0
        controller.setZoom(3.0)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        setMultiTouchControls(true)
        overlays.add(RotationGestureOverlay(this))
    }
}