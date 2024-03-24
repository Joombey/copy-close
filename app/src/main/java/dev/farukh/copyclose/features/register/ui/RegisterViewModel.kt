package dev.farukh.copyclose.features.register.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.model.Address
import dev.farukh.copyclose.features.register.data.RegisterRepository
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {
    private val _uiState = RegisterScreenUIStateMutable()
    val uiState: RegisterScreenUIState = _uiState

    private val query = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    ).also { it.tryEmit("") }

    init {
        query
            .debounce(300L)
            .filter { it != "" }
            .onEach { query ->
                when (val result = registerRepository.query(query)) {
                    RequestResult.ClientError -> _uiState.mapError = MapErr.NotSuchAddress
                    RequestResult.ServerInternalError -> _uiState.mapError = MapErr.NotSuchAddress
                    is RequestResult.Success -> {
                        _uiState._addressList.clear()
                        _uiState._addressList.addAll(result.data)

                        if (result.data.isEmpty()) {
                            _uiState.mapError = MapErr.NotSuchAddress
                        } else if (_uiState.mapError != null) {
                            _uiState.mapError = null
                        }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
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
        _uiState.query = value
        query.tryEmit(value)
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = registerRepository.register(
                name = uiState.name,
                login = uiState.login,
                password = uiState.password,
                lat = uiState.chosenAddress?.lat ?: return@launch,
                lon = uiState.chosenAddress?.lon ?: return@launch,
                addressName = uiState.chosenAddress?.addressName ?: return@launch
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
                        _uiState.mapError = null
                        _uiState.networkErr = null
                    }
                }
            }
        }
    }

    fun chooseAddress(address: Address) {
        _uiState.chosenAddress = address
    }
}

private class RegisterScreenUIStateMutable : RegisterScreenUIState {
    override val name by mutableStateOf("")
    override var login by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordConfirm by mutableStateOf("")

    override var passwordConfirmErr by mutableStateOf(false)
    override var userExistsErr by mutableStateOf(false)

    override var registered by mutableStateOf(false)

    override var mapError: MapErr? by mutableStateOf(null)
    override var networkErr: NetworkErr? by mutableStateOf(null)

    override var query by mutableStateOf("")
    override var chosenAddress: Address? by mutableStateOf(null)
    val _addressList = mutableStateListOf<Address>()
    override val addressList: List<Address> = _addressList
}

interface RegisterScreenUIState {
    val name: String
    val login: String
    val password: String
    val passwordConfirm: String

    val passwordConfirmErr: Boolean
    val userExistsErr: Boolean

    val query: String
    val addressList: List<Address>
    val chosenAddress: Address?

    val registered: Boolean

    val mapError: MapErr?
    val networkErr: NetworkErr?
}

@Stable
sealed interface MapErr {
    data object NotSuchAddress : MapErr
}

sealed interface NetworkErr {
    data object ClientErr : NetworkErr
    data object ServerErr : NetworkErr
}