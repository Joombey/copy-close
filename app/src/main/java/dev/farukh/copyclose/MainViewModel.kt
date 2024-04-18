package dev.farukh.copyclose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.repos.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    val activeUser = userRepository.activeUser
    fun makeUserActive(userID: String) = viewModelScope.launch {
        userRepository.makeUserActive(userID)
    }

    fun logOut(userID: String) = viewModelScope.launch {
        userRepository.makeUserInActive(userID)
    }
}