package dev.farukh.copyclose.features.admin.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.dto.UserInfoDTO
import dev.farukh.copyclose.core.data.models.Service
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.domain.GetOrderListUseCase
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.admin.data.repos.AdminRepository
import dev.farukh.copyclose.features.register.data.model.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val getOrderListUseCase: GetOrderListUseCase,
    private val adminRepository: AdminRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _uiState by mutableStateOf(AdminUIStateMutable())
    val uiState: AdminUIState get() = _uiState

    init {
        start()
    }


    fun start() {
        viewModelScope.launch(Dispatchers.Main) {
            loadBlockList()
            listenBlockListUpdates()
        }
    }

    private fun listenBlockListUpdates() {
        viewModelScope.launch {
            try {
                val user = userRepository.getActiveUser()!!
                adminRepository.getTriggerFlow(user.id, user.authToken!!)
                    .flowOn(Dispatchers.IO)
                    .collectLatest { loadBlockList() }
            } catch (e: Exception) {
                _uiState.error = true
            }
        }
    }

    private suspend fun loadBlockList() {
        try {
            _uiState.error = false
            _uiState.loading = true
            _uiState.dialog = null
        } catch (_: Exception) {
        }
        val user = userRepository.getActiveUser()!!
        when (val blockListResult = adminRepository.getBlockList(user.id, user.authToken!!)) {
            is Result.Error -> {
                _uiState.error = true
            }

            is Result.Success -> {
                val userMap = hashMapOf<String, UserInfoDTO>()
                _uiState._blockList.clear()
                val list: List<BlockItemUI> = blockListResult.data.map { block ->
                    if (!userMap.containsKey(block.userId)) {
                        when (val data = userRepository.getUserData(block.userId)) {
                            is Result.Error -> {
                                _uiState.error = true
                                return@map null
                            }

                            is Result.Success -> {
                                userMap[block.userId] = data.data
                            }
                        }
                    }
                    when (val orderListResult = getOrderListUseCase()) {
                        is Result.Error -> {
                            _uiState.error = true
                            null
                        }

                        is Result.Success -> {
                            BlockItemUI(
                                icon = withContext(Dispatchers.Default) {
                                    UiUtils.bytesToImage(
                                        userMap[block.userId]!!.imageData
                                    )!!
                                },
                                comment = block.orderMessage,
                                orderID = block.orderId,
                                services = orderListResult.data.first.find {
                                    it.orderID == block.orderId
                                }!!.services,
                                userName = userMap[block.userId]!!.name,
                                address = Address(
                                    addressName = userMap[block.userId]!!.addressCore.addressName,
                                    lat = userMap[block.userId]!!.addressCore.lat,
                                    lon = userMap[block.userId]!!.addressCore.lon
                                ),
                                date = block.reportDate.split(" ")[0],
                                reportID = block.reportId,
                                userId = block.userId,
                                sellerId = block.sellerId,
                                reportMessage = block.reportMessage ?: ""
                            )
                        }
                    }
                }.filterNotNull()
                _uiState._blockList.clear()
                _uiState._blockList.addAll(list)
            }
        }
        _uiState.loading = false
    }

    fun dismiss() {
        _uiState.dialog = null
    }

    fun showInfo(blockItemUI: BlockItemUI) {
        _uiState.dialog = blockItemUI
    }

    fun sendSolution(
        blockItemUI: BlockItemUI,
        block: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val activeUser = userRepository.getActiveUser()!!
            adminRepository.block(
                userID = activeUser.id,
                authToken = activeUser.authToken!!,
                userBlockId = blockItemUI.sellerId,
                reportID = blockItemUI.reportID,
                block = block,
            )
        }
    }
}

private class AdminUIStateMutable : AdminUIState {
    override var dialog: BlockItemUI? by mutableStateOf(null)
    override var error: Boolean by mutableStateOf(false)
    override var loading: Boolean by mutableStateOf(true)
    val _blockList = mutableStateListOf<BlockItemUI>()
    override val blockList: List<BlockItemUI> get() = _blockList

}

@Stable
interface AdminUIState {
    val error: Boolean
    val loading: Boolean
    val dialog: BlockItemUI?
    val blockList: List<BlockItemUI>
}

class BlockItemUI(
    val reportID: String,
    val icon: ImageBitmap,
    val userName: String,
    val reportMessage: String,
    val userId: String,
    val sellerId: String,
    val address: Address,
    val comment: String,
    val orderID: String,
    val services: List<Service>,
    val date: String,
) {
    val priceTotal: Int = services.sumOf { it.price * it.amount }
}