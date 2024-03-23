package dev.farukh.copyclose

import android.app.Application
import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import db.farukh.CopyCloseDB
import dev.farukh.copyclose.db.Database
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.osmdroid.config.Configuration

class CopyCloseApp : Application(), DIAware {
    override fun onCreate() {
        loadMap()
        super.onCreate()
    }

    override val di = DI {
        bindProvider {
            val androidDriver = AndroidSqliteDriver(CopyCloseDB.Schema, this@CopyCloseApp, "test.db")
            Database(CopyCloseDB(androidDriver))
        }

        bindProvider {
            MainViewModel(instance<Database>())
        }
    }

    private fun loadMap() {
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = externalCacheDir
            load(this@CopyCloseApp, getSharedPreferences("TILE_CACHE_PREF", Context.MODE_PRIVATE))
        }
    }
}