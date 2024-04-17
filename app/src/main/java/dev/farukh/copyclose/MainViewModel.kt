package dev.farukh.copyclose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.Screen
import dev.farukh.copyclose.core.data.repos.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.AuthGraph.Auth)
    val currentScreen = _currentScreen.asStateFlow()

    val activeUser = userRepository.activeUser
        .onEach { userID ->
            if (userID == null) {
                toAuth()
            } else {
                toMap()
            }
        }

    fun toAuth() { _currentScreen.update { Screen.AuthGraph.Auth } }

    fun toRegister() { _currentScreen.update { Screen.AuthGraph.Register } }

    fun toMap() { _currentScreen.update { Screen.Map } }

    fun toOrders(userID: String) { _currentScreen.update { Screen.Orders(userID) } }

    fun toProfile(userID: String) { _currentScreen.update { Screen.Profile(userID) } }

    fun makeUserActive(userID: String) {
        viewModelScope.launch {
            userRepository.makeUserActive(userID)
        }
    }
}