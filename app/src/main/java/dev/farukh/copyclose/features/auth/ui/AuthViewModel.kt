package dev.farukh.copyclose.features.auth.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.network.services.copyClose.authService.AuthService
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class AuthViewModel(
    private val authService: AuthService,
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

    fun login() = viewModelScope.async(Dispatchers.IO) {
        when(val result = authService.signIn(uiState.login, uiState.password)) {
            RequestResult.ClientError -> {
                _errChannel.send(AuthErrors.ErrorClient)
                false
            }
            RequestResult.ServerInternalError -> {
                _errChannel.send(AuthErrors.ServerError)
                false
            }
            is RequestResult.Success -> result.data
        }
    }
}

sealed interface AuthErrors {
    data object ErrorClient : AuthErrors
    data object ServerError : AuthErrors
    data object ErrorCredentials : AuthErrors
}

private class AuthScreenUIStateMutable : AuthScreenUIState {
    override var login: String by mutableStateOf("")
    override var password: String by mutableStateOf("")
}

@Stable
interface AuthScreenUIState {
    val login: String
    val password: String
}