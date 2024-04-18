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
import dev.farukh.copyclose.features.register.data.dto.RegisterDTO
import dev.farukh.copyclose.features.register.data.model.Address
import dev.farukh.copyclose.features.register.data.repos.GeoRepository
import dev.farukh.copyclose.features.register.data.repos.MediaRepository
import dev.farukh.copyclose.features.register.domain.RegisterUseCase
import dev.farukh.copyclose.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val geoRepository: GeoRepository,
    private val mediaRepository: MediaRepository,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {
    private val _uiState = RegisterScreenUIStateMutable()
    val uiState: RegisterScreenUIState = _uiState

    fun query() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = geoRepository.query(uiState.queryUIState.query)) {
            is Result.Error -> {
                _uiState._queryUIState.queryError = QueryErr.NoSuchAddress
            }

            is Result.Success -> {
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

    fun setName(value: String) {
        _uiState.name = value
    }

    fun register() = viewModelScope.launch(Dispatchers.IO) {
        val registerDTO = RegisterDTO(
            login = uiState.login,
            password = uiState.password,
            name = uiState.name,
            address = uiState.queryUIState.chosenAddress ?: return@launch,
            image = _uiState.userIconUri ?: return@launch,
            isSeller = uiState.isSeller,
        )
        when (val result = registerUseCase(registerDTO)) {
            is Result.Error -> {
                _uiState.networkErr = NetworkErr.ClientErr
            }

            is Result.Success -> {
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


    fun chooseAddress(address: Address) {
        _uiState._queryUIState.chosenAddress = address
        _uiState._queryUIState.query = address.addressName
        _uiState._queryUIState.addressListShown = false
    }

    fun chooseIcon(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.userIcon = mediaRepository.getImage(uri)
        if (uiState.userIcon != null) {
            _uiState.userIconUri = uri
        } else {
            _uiState.userIconUri = null
        }
    }


    fun sellerChange(b: Boolean) {
        _uiState.isSeller = b
    }
}

private class RegisterScreenUIStateMutable : RegisterScreenUIState {
    val _queryUIState = QueryUIStateMutable()
    override val queryUIState: QueryUIState = _queryUIState

    override var isSeller: Boolean by mutableStateOf(false)
    override var name by mutableStateOf("")
    override var login by mutableStateOf("")
    override var password by mutableStateOf("")
    override var passwordConfirm by mutableStateOf("")

    override var registered by mutableStateOf(false)
    override var userExistsErr by mutableStateOf(false)
    override var passwordConfirmErr by mutableStateOf(false)

    var userIconUri: Uri? = null
    override var userIcon: ImageBitmap? by mutableStateOf(null)
    override var networkErr: NetworkErr? by mutableStateOf(null)
}

private class QueryUIStateMutable : QueryUIState {
    val _addressList = mutableStateListOf<Address>()
    override val addressList: List<Address> = _addressList

    override var query: String by mutableStateOf("")
    override var queryError: QueryErr? by mutableStateOf(null)
    override var chosenAddress: Address? by mutableStateOf(null)
    override var addressListShown: Boolean by mutableStateOf(false)
}

interface RegisterScreenUIState {
    val isSeller: Boolean
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