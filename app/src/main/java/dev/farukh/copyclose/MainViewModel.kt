package dev.farukh.copyclose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import db.farukh.SampleEntity
import dev.farukh.copyclose.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val db: Database) : ViewModel() {
    val list: Flow<List<SampleEntity>> get() = db.getAll()

    fun newUser() {
        viewModelScope.launch(Dispatchers.IO) {
            db.newUser("abc")
        }
    }
    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.delete(id)
        }
    }
}