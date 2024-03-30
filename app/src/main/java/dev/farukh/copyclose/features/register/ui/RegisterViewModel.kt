package dev.farukh.copyclose.features.register.ui

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.model.Address
import dev.farukh.copyclose.features.register.data.RegisterRepository
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {
    private val _uiState = RegisterScreenUIStateMutable()
    val uiState: RegisterScreenUIState = _uiState

    fun query() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = registerRepository.query(uiState.mapUIState.query)) {
                RequestResult.ClientError -> _uiState._mapUIState.mapError = MapErr.NotSuchAddress
                RequestResult.ServerInternalError -> _uiState._mapUIState.mapError = MapErr.NotSuchAddress
                is RequestResult.Success -> {
                    _uiState._mapUIState._addressList.clear()
                    _uiState._mapUIState._addressList.addAll(result.data)

                    if (result.data.isEmpty()) {
                        _uiState._mapUIState.mapError = MapErr.NotSuchAddress
                    } else if (_uiState._mapUIState.mapError != null) {
                        _uiState._mapUIState.mapError = null
                    }
                }
            }
        }
    }

    fun setLogin(value: String) {
        _uiState.login = value
        if (uiState.userExistsErr) {
            _uiState.userExistsErr = false
        }
    }

    fun setPassword(value: String) {
        _uiState.password = value
    }

    fun setPasswordConfirm(value: String) {
        _uiState.passwordConfirm = value
        _uiState.passwordConfirmErr = uiState.passwordConfirm == uiState.password
    }

    fun setQuery(value: String) {
        _uiState._mapUIState.query = value
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = registerRepository.register(
                name = uiState.name,
                login = uiState.login,
                password = uiState.password,
                lat = uiState.mapUIState.chosenAddress?.lat ?: return@launch,
                lon = uiState.mapUIState.chosenAddress?.lon ?: return@launch,
                addressName = uiState.mapUIState.chosenAddress?.addressName ?: return@launch
            )

            when (result) {
                RequestResult.ClientError -> {
                    _uiState.networkErr = NetworkErr.ClientErr
                }

                RequestResult.ServerInternalError -> {
                    _uiState.networkErr = NetworkErr.ServerErr
                }

                is RequestResult.Success -> {
                    _uiState.registered = result.data
                    _uiState.userExistsErr = !result.data

                    if (uiState.registered) {
                        _uiState.passwordConfirmErr = false
                        _uiState._mapUIState.mapError = null
                        _uiState.networkErr = null
                    }
                }
            }
        }
    }

    fun chooseAddress(address: Address) {
        _uiState._mapUIState.chosenAddress = address
    }
}

private class RegisterScreenUIStateMutable : RegisterScreenUIState {
    val _mapUIState = MapUIStateMutable()
    override val mapUIState: MapUIState = _mapUIState

    override val name by mutableStateOf("")
    override var login by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordConfirm by mutableStateOf("")

    override var passwordConfirmErr by mutableStateOf(false)
    override var userExistsErr by mutableStateOf(false)

    override var registered by mutableStateOf(false)

    override var networkErr: NetworkErr? by mutableStateOf(null)
}

private class MapUIStateMutable: MapUIState {
    val _addressList = mutableStateListOf<Address>()

    override var query: String by mutableStateOf("")
    override val addressList: List<Address> = _addressList
    override var chosenAddress: Address? by mutableStateOf(null)
    override var mapError: MapErr? by mutableStateOf(null)
}

interface RegisterScreenUIState {
    val name: String
    val login: String
    val password: String
    val passwordConfirm: String

    val passwordConfirmErr: Boolean
    val userExistsErr: Boolean

    val mapUIState: MapUIState

    val registered: Boolean
    val networkErr: NetworkErr?
}

@Stable
interface MapUIState {
    val query: String
    val addressList: List<Address>
    val chosenAddress: Address?
    val mapError: MapErr?
}

@Stable
sealed interface MapErr {
    data object NotSuchAddress : MapErr
}

sealed interface NetworkErr {
    data object ClientErr : NetworkErr
    data object ServerErr : NetworkErr
}