package dev.farukh.copyclose

import android.app.Application
import android.content.Context
import org.osmdroid.config.Configuration

class CopyCloseApp : Application() {
    override fun onCreate() {
        loadMap()
        super.onCreate()
    }

    private fun loadMap() {
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = externalCacheDir
            load(this@CopyCloseApp, getSharedPreferences("TILE_CACHE_PREF", Context.MODE_PRIVATE))
        }
    }
}