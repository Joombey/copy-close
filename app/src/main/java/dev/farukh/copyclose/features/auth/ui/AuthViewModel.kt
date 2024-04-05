package dev.farukh.copyclose.features.auth.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.domain.LoginUseCase
import dev.farukh.copyclose.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _errChannel = Channel<AuthErrors>()
    val errChannel: ReceiveChannel<AuthErrors> = _errChannel
    private val _uiState = AuthScreenUIStateMutable()
    val uiState: AuthScreenUIState = _uiState

    fun setLogin(login: String) {
        _uiState.login = login
    }

    fun setPassword(pass: String) {
        _uiState.password = pass
    }

    fun logIn() = viewModelScope.launch(Dispatchers.IO) {
        when (val loginResult = loginUseCase(uiState.login, uiState.password)) {
            is Result.Error -> {}
            is Result.Success -> _uiState.loggedIn = loginResult.data
        }
    }
}

sealed interface AuthErrors {
    data object ErrorClient : AuthErrors
    data object ServerError : AuthErrors
    data object ErrorCredentials : AuthErrors
}

private class AuthScreenUIStateMutable : AuthScreenUIState {
    override var loggedIn: String? by mutableStateOf(null)
    override var login: String by mutableStateOf("")
    override var password: String by mutableStateOf("")
}

@Stable
interface AuthScreenUIState {
    val loggedIn: String?
    val login: String
    val password: String
}