package dev.farukh.copyclose.core

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import db.CopyCloseDB
import dev.farukh.copyclose.MainViewModel
import dev.farukh.copyclose.core.data.repos.AuthRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.core.data.source.UserRemoteDataSource
import dev.farukh.copyclose.core.utils.MediaInserter
import dev.farukh.network.networkDI
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal fun coreDI(appDI: DI) = DI {
    extend(appDI)
    import(networkDI)

    bindProvider<SqlDriver> { AndroidSqliteDriver(CopyCloseDB.Schema, instance(), "db") }

    bindSingleton<CopyCloseDB> { CopyCloseDB(instance()) }

    bindProvider { UserLocalDataSource(instance()) }

    bindProvider { MediaInserter(instance<Context>().contentResolver) }

    bindProvider { UserRemoteDataSource(instance(), instance(), instance()) }

    bindProvider { UserRepository(instance(), instance()) }

    bindProvider { AuthRepository(instance()) }

    //ViewModels
    bindProvider { MainViewModel( instance() ) }
}