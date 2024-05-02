package dev.farukh.copyclose.features.profile.ui

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.MediaManager
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.network.core.ServiceCore
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val mediaManager: MediaManager,
    private val userID: String
) : ViewModel(), ProfileActions {
    private var _uiState by mutableStateOf<ProfileUIState>(ProfileUIState.Loading)
    val uiState: ProfileUIState get() = _uiState

    init { getUser(userID) }

    fun getUser(userID: String): Job {
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
                                if (servicesMutable.isNotEmpty()) {
                                    servicesMutable.clear()
                                }
                                servicesMutable.addAll(userInfoDTO.services)
                                isSeller = userInfoDTO.isSeller
                                icon = UiUtils.bytesToImage(userInfoDTO.imageData)

                                idsToDelete.clear()
                                iconUri = null
                            }
                        }

                        else -> {
                            _uiState = ProfileDataMutable().apply {
                                canEditProfile = currentUser?.id == userID
                                name = userInfoDTO.name
                                if (servicesMutable.isNotEmpty()) {
                                    servicesMutable.clear()
                                }
                                servicesMutable.addAll(userInfoDTO.services)
                                isSeller = userInfoDTO.isSeller
                                icon = UiUtils.bytesToImage(userInfoDTO.imageData)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun setTitle(index: Int, newTitle: String) {
        (_uiState as? ProfileDataMutable)?.apply {
            servicesMutable[index] = services[index].copy(title = newTitle)
        }
    }

    override fun setPrice(index: Int, newPrice: Int) {
        (_uiState as? ProfileDataMutable)?.apply {
            servicesMutable[index] = services[index].copy(price = newPrice)
        }
    }

    override fun removeServiceAt(index: Int) {
        (_uiState as? ProfileDataMutable)?.apply {
            services[index].id?.let { idToDelete ->
                idsToDelete += idToDelete
            }
            servicesMutable.removeAt(index)
        }
    }

    override fun addService() {
        (_uiState as? ProfileDataMutable)?.apply {
            servicesMutable += ServiceCore(
                title = "Название услуги",
                price = 100
            )
        }
    }

    override fun setIcon(newIcon: Uri) = viewModelScope.launch {
        (_uiState as? ProfileDataMutable)?.apply {
            mediaManager.getMediaByUri(newIcon)?.let { imageRaw ->
                iconUri = newIcon
                icon = UiUtils.bytesToImage(imageRaw)
            }
        }
    }

    override fun saveChanges() = viewModelScope.launch {
        (_uiState as? ProfileDataMutable)?.updating = true
        (_uiState as? ProfileDataMutable)?.apply {
            val editResult = userRepository.editProfile(
                idsToDelete = idsToDelete,
                services = services,
                name = name,
                newImageUri = iconUri,
            )
            editing = false
            if (editResult is Result.Error) getUser(userID)
            else {
                when(val getUserResult = userRepository.getUserData(userID)) {
                    is Result.Error -> {
                        _uiState = ProfileUIState.Error
                        return@launch
                    }
                    is Result.Success -> {
                        getUserResult.data.services.forEach { service ->
                            val index = servicesMutable.indexOfFirst {
                                it.title == service.title &&
                                        it.price == service.price
                            }
                            if (index == -1) {
                                servicesMutable += service
                            } else {
                                servicesMutable[index] = service
                            }
                        }
                    }
                }
            }
            updating = false
        }
    }

    override fun startEdit() { (_uiState as? ProfileDataMutable)?.editing = true }
    override fun setName(newName: String) { (_uiState as? ProfileDataMutable)?.name = newName }
}

@Stable
private class ProfileDataMutable(
    name: String = "",
    icon: ImageBitmap? = null,
    canEditProfile: Boolean = false,
    services: List<ServiceCore> = emptyList(),
    isSeller: Boolean = false,
    editing: Boolean = false
) : ProfileUIState.ProfileData {

    var iconUri: Uri? = null

    override var canEditProfile: Boolean by mutableStateOf(canEditProfile)
    val idsToDelete: MutableList<String> = mutableListOf()

    val servicesMutable: SnapshotStateList<ServiceCore> = services.toMutableStateList()
    override val services: List<ServiceCore> = servicesMutable

    override var icon: ImageBitmap? by mutableStateOf(icon)
    override var name: String by mutableStateOf(name)
    override var isSeller: Boolean by mutableStateOf(isSeller)
    override var editing: Boolean by mutableStateOf(editing)
    override var updating: Boolean by mutableStateOf(false)
}

@Stable
sealed interface ProfileUIState {
    data object Loading : ProfileUIState
    data object Error : ProfileUIState
    interface ProfileData : ProfileUIState {
        val name: String
        val icon: ImageBitmap?
        val canEditProfile: Boolean
        val editing: Boolean
        val isSeller: Boolean
        val services: List<ServiceCore>
        val updating: Boolean
    }
}