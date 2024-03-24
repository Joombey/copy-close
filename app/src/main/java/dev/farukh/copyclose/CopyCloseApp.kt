package dev.farukh.copyclose

import android.app.Application
import android.content.Context
import dev.farukh.copyclose.features.auth.authDI
import dev.farukh.copyclose.features.register.registerDI
import dev.farukh.network.di.networkDI
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindInstance
import org.kodein.di.bindProvider
import org.osmdroid.config.Configuration

class CopyCloseApp : Application(), DIAware {
    override fun onCreate() {
        loadMap()
        extendDI()
        super.onCreate()
    }

    override val di = DI {
        import(networkDI)
        bindProvider { applicationContext }
        bindProvider { MainViewModel() }
    }

    private fun extendDI() {
//        registerDI(di)
//        authDI(di)
    }

    private fun loadMap() {
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = externalCacheDir
            load(this@CopyCloseApp, getSharedPreferences("TILE_CACHE_PREF", Context.MODE_PRIVATE))
        }
    }
}