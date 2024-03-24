package dev.farukh.copyclose.db
//
//import app.cash.sqldelight.coroutines.asFlow
//import app.cash.sqldelight.coroutines.mapToList
//import db.farukh.CopyCloseDB
//import kotlinx.coroutines.Dispatchers


//class Database(
//    private val db: CopyCloseDB,
//) {
//    fun getAll() = db.otherQueries.getAll().asFlow().mapToList(Dispatchers.IO)
//    suspend fun newUser(name: String) {
//        db.otherQueries.insertUser(name)
//    }
//
//    suspend fun delete(id: Long) {
//        db.sample2Queries.query3()
//        db.otherQueries.delete(id)
//    }
//}