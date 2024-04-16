package dev.farukh.copyclose

import androidx.lifecycle.ViewModel
import dev.farukh.copyclose.core.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.AuthGraph.Auth)
    val currentScreen = _currentScreen.asStateFlow()

    fun toAuth() {
        _currentScreen.update { Screen.AuthGraph.Auth }
    }

    fun toRegister() {
        _currentScreen.update { Screen.AuthGraph.Register }
    }

    fun toMap(userID: String) {
        _currentScreen.update {
            Screen.MapScreen(userID)
        }
    }
}