package dev.farukh.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView


@Composable
fun Map(modifier: Modifier) {
    var mapReady by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val provider: IConfigurationProvider = Configuration.getInstance()
            provider.userAgentValue = context.applicationContext.packageName
            provider.osmdroidTileCache = context.externalCacheDir
            provider.load(
                context,
                context.getSharedPreferences(TILE_CACHE_PREF, Context.MODE_PRIVATE)
            )
            mapReady = true
        }
    }
    if (mapReady) {
        AndroidView(
            factory = {
                MapView(it).apply {
                    setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                    maxZoomLevel = 18.0
                    minZoomLevel = 3.0
                    controller.setZoom(7.0)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                }
            },
            modifier = modifier,
            update = {

            }
        )
    } else {
        Box(modifier = modifier) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}

const val TILE_CACHE_PREF = "tiles"