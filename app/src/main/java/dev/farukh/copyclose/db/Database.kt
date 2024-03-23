package dev.farukh.copyclose.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import db.farukh.CopyCloseDB
import kotlinx.coroutines.Dispatchers


class Database(
    private val db: CopyCloseDB,
) {
    fun getAll() = db.sampleQueries.getAll().asFlow().mapToList(Dispatchers.IO)
    suspend fun newUser(name: String) {
        db.sampleQueries.insertUser(name)
    }

    suspend fun delete(id: Long) {
        db.sampleQueries.delete(id)
    }
}