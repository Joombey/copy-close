package dev.farukh.copyclose.features.register.ui

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.model.Address
import dev.farukh.copyclose.features.register.data.MediaContentRepository
import dev.farukh.copyclose.features.register.data.RegisterRepository
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository,
    private val mediaContentRepository: MediaContentRepository,
) : ViewModel() {
    private val _uiState = RegisterScreenUIStateMutable()
    val uiState: RegisterScreenUIState = _uiState

    fun query() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = registerRepository.query(uiState.queryUIState.query)) {
                RequestResult.ClientError -> _uiState._queryUIState.queryError = QueryErr.NoSuchAddress
                RequestResult.ServerInternalError -> _uiState._queryUIState.queryError = QueryErr.NoSuchAddress
                is RequestResult.Success -> {
                    _uiState._queryUIState._addressList.clear()
                    _uiState._queryUIState._addressList.addAll(result.data)

                    if (result.data.isEmpty()) {
                        _uiState._queryUIState.queryError = QueryErr.NoSuchAddress
                    } else if (_uiState._queryUIState.queryError != null) {
                        _uiState._queryUIState.queryError = null
                    }
                }
            }
            _uiState._queryUIState.addressListShown = true
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
        _uiState._queryUIState.query = value
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = registerRepository.register(
                name = uiState.name,
                login = uiState.login,
                password = uiState.password,
                lat = uiState.queryUIState.chosenAddress?.lat ?: return@launch,
                lon = uiState.queryUIState.chosenAddress?.lon ?: return@launch,
                addressName = uiState.queryUIState.chosenAddress?.addressName ?: return@launch
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
                        _uiState._queryUIState.queryError = null
                        _uiState.networkErr = null
                    }
                }
            }
        }
    }

    fun chooseAddress(address: Address) {
        _uiState._queryUIState.chosenAddress = address
        _uiState._queryUIState.query = address.addressName
        _uiState._queryUIState.addressListShown = false
    }

    fun chooseIcon(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.userIcon = mediaContentRepository.getImage(uri)
        }
    }
}

private class RegisterScreenUIStateMutable : RegisterScreenUIState {
    val _queryUIState = QueryUIStateMutable()
    override val queryUIState: QueryUIState = _queryUIState

    override val name by mutableStateOf("")
    override var login by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordConfirm by mutableStateOf("")

    override var registered by mutableStateOf(false)
    override var userExistsErr by mutableStateOf(false)
    override var passwordConfirmErr by mutableStateOf(false)


    override var userIcon: ImageBitmap? by mutableStateOf(null)
    override var networkErr: NetworkErr? by mutableStateOf(null)
}

private class QueryUIStateMutable: QueryUIState {
    val _addressList = mutableStateListOf<Address>()
    override val addressList: List<Address> = _addressList

    override var query: String by mutableStateOf("")
    override var queryError: QueryErr? by mutableStateOf(null)
    override var chosenAddress: Address? by mutableStateOf(null)
    override var addressListShown: Boolean by mutableStateOf(false)
}

interface RegisterScreenUIState {
    val name: String
    val login: String
    val password: String
    val passwordConfirm: String

    val passwordConfirmErr: Boolean
    val userExistsErr: Boolean

    val queryUIState: QueryUIState
    val userIcon: ImageBitmap?

    val registered: Boolean
    val networkErr: NetworkErr?
}

@Stable
interface QueryUIState {
    val query: String
    val addressList: List<Address>
    val chosenAddress: Address?
    val addressListShown: Boolean
    val queryError: QueryErr?
}

@Stable
sealed interface QueryErr {
    data object NoSuchAddress : QueryErr
}

sealed interface NetworkErr {
    data object ClientErr : NetworkErr
    data object ServerErr : NetworkErr
}