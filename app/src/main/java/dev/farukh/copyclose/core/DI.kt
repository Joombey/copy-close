package dev.farukh.copyclose.core

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import db.CopyCloseDB
import dev.farukh.copyclose.core.data.repos.AuthRepository
import dev.farukh.copyclose.core.data.repos.MediaRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.core.data.source.UserRemoteDataSource
import dev.farukh.copyclose.core.domain.LoginUseCase
import dev.farukh.copyclose.core.domain.RegisterUseCase
import dev.farukh.copyclose.utils.MediaInserter
import dev.farukh.network.networkDI
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal fun coreDI(appDI: DI) = DI {
    extend(appDI)
    import(networkDI)

    bindProvider<SqlDriver> { AndroidSqliteDriver(CopyCloseDB.Schema, instance()) }

    bindSingleton<CopyCloseDB> { CopyCloseDB(instance()) }

    bindProvider { UserLocalDataSource(instance()) }

    bindProvider { MediaInserter(instance<Context>().contentResolver) }

    bindProvider { UserRemoteDataSource(instance(), instance(), instance()) }

    bindProvider { UserRepository(instance(), instance()) }

    bindProvider { MediaRepository(instance()) }

    bindProvider { AuthRepository(instance()) }

    bindProvider { RegisterUseCase(instance(), instance(), instance()) }

    bindProvider { LoginUseCase(instance(), instance()) }
}