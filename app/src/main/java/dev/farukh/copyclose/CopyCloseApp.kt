package dev.farukh.copyclose

import android.app.Application
import android.content.Context
import dev.farukh.copyclose.core.coreDI
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindProvider
import org.osmdroid.config.Configuration

class CopyCloseApp : Application(), DIAware {
    override fun onCreate() {
        loadMap()
        super.onCreate()
    }

    private val appDI = DI { bindProvider { applicationContext } }

    override val di = coreDI(appDI)

    private fun loadMap() {
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = externalCacheDir
            load(this@CopyCloseApp, getSharedPreferences("TILE_CACHE_PREF", Context.MODE_PRIVATE))
        }
    }
}