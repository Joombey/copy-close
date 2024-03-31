package dev.farukh.copyclose.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import db.CopyCloseDB
import dev.farukh.copyclose.core.data.repos.MediaRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.domain.CreateUserUseCase
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal fun coreDI(appDI: DI) = DI {
    extend(appDI)

    bindProvider<SqlDriver> { AndroidSqliteDriver(CopyCloseDB.Schema, instance()) }

    bindSingleton<CopyCloseDB> { CopyCloseDB(instance()) }

    bindProvider { UserRepository(instance()) }

    bindProvider { MediaRepository(instance()) }

    bindProvider { CreateUserUseCase(instance(), instance(), instance()) }
}