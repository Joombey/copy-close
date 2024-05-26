package dev.farukh.copyclose.features.order.creation.ui

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.models.MediaInfo
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.MediaManager
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.creation.data.dto.OrderCreationDTO
import dev.farukh.copyclose.features.order.creation.domain.CreateOrderUseCase
import dev.farukh.copyclose.features.order.creation.domain.OrderCreationStage
import dev.farukh.network.core.ServiceCore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderCreationViewModel(
    private val sellerID: String,
    private val userRepository: UserRepository,
    private val mediaManager: MediaManager,
    private val createOrderUseCase: CreateOrderUseCase,
) : ViewModel(), OrderCreationActions {

    private var _uiState by mutableStateOf<OrderCreationUIState>(OrderCreationUIState.Loading)
    val uiState: OrderCreationUIState get() = _uiState

    init {
        getUserData()
    }

    fun getUserData() = viewModelScope.launch {
        _uiState = when (val getUserResult = userRepository.getUserData(sellerID)) {
            is Result.Error -> OrderCreationUIState.Error
            is Result.Success -> {
                OrderCreationDataMutable(
                    icon = UiUtils.bytesToImage(getUserResult.data.imageData)!!,
                    name = getUserResult.data.name,
                    address = getUserResult.data.addressCore.addressName,
                    services = getUserResult.data.services.map { it to 0 }
                )
            }
        }
    }

    override fun create() = viewModelScope.launch {
        val activeUser = userRepository.getActiveUser() ?: return@launch
        (_uiState as? OrderCreationDataMutable)?.apply {
            creationState = CreationUIState.StartSending
            val dto = OrderCreationDTO(
                userID = activeUser.id,
                authToken = activeUser.authToken!!,
                sellerID = sellerID,
                comment = comment,
                attachments = attachedFiles,
                services = services.map { it.first.id!! to it.second }.filterNot { it.second == 0 }
            )
            createOrderUseCase(dto).collectLatest { creationStage ->
                when (creationStage) {
                    is OrderCreationStage.LoadingFiles -> {
                        (creationState as? CreationUploadingFileMutable)?.apply {
                            progress = creationStage.progress
                        } ?: run {
                            creationState = CreationUploadingFileMutable(creationStage.progress)
                        }
                    }

                    is OrderCreationStage.LoadingInfo -> creationState = CreationUIState.SendingInfo
                    is OrderCreationStage.Success -> creationState = CreationUIState.Success
                    is OrderCreationStage.Error -> creationState = CreationUIState.ErrorCreation
                }
            }
        }
    }

    override fun creationInfoConfirmed() {
        (_uiState as? OrderCreationDataMutable)?.creationState = CreationUIState.Idle
    }

    override fun addAmount(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            serviceMutable[index] = serviceMutable[index].let {
                it.copy(second = it.second + 1)
            }
            if (!canOrder) {
                canOrder = true
            }
        }
    }

    override fun removeAmount(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            if (serviceMutable[index].second == 0) return@apply

            serviceMutable[index] = serviceMutable[index].let {
                it.copy(second = it.second - 1)
            }
            if (canOrder) {
                canOrder = serviceMutable.any { it.second > 0 }
            }
        }
    }

    override fun attachFile(uri: Uri) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            attachedFilesMutable += mediaManager.getMediaInfo(uri) ?: return
        }
    }

    override fun detachFile(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            attachedFilesMutable.removeAt(index)
        }
    }

    override fun changeComment(comment: String) {
        (_uiState as? OrderCreationDataMutable)?.comment = comment
    }
}

private class OrderCreationDataMutable(
    name: String,
    address: String,
    icon: ImageBitmap,
    services: List<Pair<ServiceCore, Int>>,
) : OrderCreationUIState.OrderCreationData {
    override var icon: ImageBitmap by mutableStateOf(icon)
    override var comment by mutableStateOf("")
    override var canOrder by mutableStateOf(false)
    override var address by mutableStateOf(address)
    override var name by mutableStateOf(name)

    val serviceMutable: SnapshotStateList<Pair<ServiceCore, Int>> = services.toMutableStateList()
    override val services: List<Pair<ServiceCore, Int>> = serviceMutable

    val attachedFilesMutable: SnapshotStateList<MediaInfo> = mutableStateListOf()
    override val attachedFiles: List<MediaInfo> = attachedFilesMutable

    override var creationState: CreationUIState by mutableStateOf(CreationUIState.Idle)
}

@Stable
sealed interface OrderCreationUIState {
    data object Loading : OrderCreationUIState
    data object Error : OrderCreationUIState
    interface OrderCreationData : OrderCreationUIState {
        val name: String
        val address: String
        val icon: ImageBitmap
        val comment: String
        val attachedFiles: List<MediaInfo>
        val services: List<Pair<ServiceCore, Int>>
        val canOrder: Boolean

        val creationState: CreationUIState
    }
}


private class CreationUploadingFileMutable(initialProgress: Float): CreationUIState.UploadingFile {
    override var progress: Float by mutableFloatStateOf(initialProgress)
}

@Stable
sealed interface CreationUIState {
    val canDismiss: Boolean
    data object Idle : CreationUIState {
        override val canDismiss: Boolean = true
    }
    data object ErrorCreation : CreationUIState {
        override val canDismiss: Boolean = true
    }
    data object Success : CreationUIState {
        override val canDismiss: Boolean = false
    }
    data object SendingInfo: CreationUIState {
        override val canDismiss: Boolean = false
    }
    data object StartSending: CreationUIState {
        override val canDismiss: Boolean = false
    }

    @Stable
    interface UploadingFile : CreationUIState {
        val progress: Float
        override val canDismiss: Boolean get() = false
    }
}