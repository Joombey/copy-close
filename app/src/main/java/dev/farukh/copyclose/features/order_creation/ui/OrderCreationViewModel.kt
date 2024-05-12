package dev.farukh.copyclose.features.order_creation.ui

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
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
import dev.farukh.network.core.ServiceCore
import kotlinx.coroutines.launch

class OrderCreationViewModel(
    private val sellerID: String,
    private val userRepository: UserRepository,
    private val mediaManager: MediaManager,
): ViewModel(), OrderCreationActions{

    private var _uiState by mutableStateOf<OrderCreationUIState>(OrderCreationUIState.Loading)
    val uiState: OrderCreationUIState get() = _uiState

    init { getUserData() }

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

    override fun create() {

    }

    override fun addAmount(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            _services[index] = _services[index].let {
                it.copy(second = it.second + 1)
            }
            if (!canOrder) {
                canOrder = true
            }
        }
    }

    override fun removeAmount(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            if (_services[index].second == 0) return@apply

            _services[index] = _services[index].let {
                it.copy(second = it.second - 1)
            }
            if (canOrder) {
                canOrder = _services.any { it.second > 0 }
            }
        }
    }

    override fun attachFile(uri: Uri) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            _attachedFiles += mediaManager.getMediaInfo(uri) ?: return
        }
    }

    override fun detachFile(index: Int) {
        (_uiState as? OrderCreationDataMutable)?.apply {
            _attachedFiles.removeAt(index)
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
): OrderCreationUIState.OrderCreationData {
    override var icon: ImageBitmap by mutableStateOf(icon)
    override var comment by mutableStateOf("")
    override var canOrder by mutableStateOf(false)
    override var address by mutableStateOf(address)
    override var name by mutableStateOf(name)

    val _services: SnapshotStateList<Pair<ServiceCore, Int>> = services.toMutableStateList()
    override val services: List<Pair<ServiceCore, Int>> = _services

    val _attachedFiles: SnapshotStateList<MediaInfo> = mutableStateListOf()
    override val attachedFiles: List<MediaInfo> = _attachedFiles
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
    }
}

