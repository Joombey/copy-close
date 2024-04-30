package dev.farukh.copyclose.features.profile.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.models.ServiceCategory
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    userID: String
) : ViewModel() {
    private var _uiState by mutableStateOf<ProfileUIState>(ProfileUIState.Loading)
    val uiState: ProfileUIState get() = _uiState

    init {
        getUser(userID)
    }

    fun getUser(userID: String): Job {
        _uiState = ProfileUIState.Loading
        return viewModelScope.launch {
            when (val userDataResult = userRepository.getUserData(userID)) {
                is Result.Error -> {
                    _uiState = ProfileUIState.Error
                }

                is Result.Success -> {
                    val currentUser = userRepository.getActiveUser()
                    val userInfoDTO = userDataResult.data
                    when (_uiState) {
                        is ProfileDataMutable -> {
                            (_uiState as ProfileDataMutable).apply {
                                canEditProfile = currentUser?.id == userInfoDTO.userID
                                name = userInfoDTO.name
                                categories = userInfoDTO.categories
                                isSeller = userInfoDTO.isSeller
                                icon = UiUtils.bytesToImage(userInfoDTO.imageData)
                            }
                        }

                        else -> {
                            _uiState = ProfileDataMutable().apply {
                                canEditProfile = currentUser?.id == userID
                                name = userInfoDTO.name
                                categories = userInfoDTO.categories
                                isSeller = userInfoDTO.isSeller
                                icon = UiUtils.bytesToImage(userInfoDTO.imageData)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Stable
private class ProfileDataMutable(
    name: String = "",
    icon: ImageBitmap? = null,
    canEditProfile: Boolean = false,
    categories: List<ServiceCategory> = emptyList(),
    isSeller: Boolean = false
) : ProfileUIState.ProfileData {
    override var name: String by mutableStateOf(name)
    override var icon: ImageBitmap? by mutableStateOf(icon)
    override var canEditProfile: Boolean by mutableStateOf(canEditProfile)
    override var categories: List<ServiceCategory> by mutableStateOf(categories)
    override var isSeller: Boolean by mutableStateOf(isSeller)
}

@Stable
sealed interface ProfileUIState {
    data object Loading : ProfileUIState
    data object Error : ProfileUIState
    interface ProfileData : ProfileUIState {
        val name: String
        val icon: ImageBitmap?
        val canEditProfile: Boolean
        val isSeller: Boolean
        val categories: List<ServiceCategory>
    }
}