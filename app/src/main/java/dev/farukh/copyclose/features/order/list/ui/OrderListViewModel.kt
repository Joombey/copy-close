package dev.farukh.copyclose.features.order.list.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.data.models.Service
import dev.farukh.copyclose.core.domain.GetOrderListUseCase
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.list.data.dto.Attachment
import dev.farukh.copyclose.features.order.list.data.dto.OrderDTO
import dev.farukh.copyclose.features.order.list.data.dto.OrderState
import dev.farukh.copyclose.features.order.list.domain.ReportUseCase
import dev.farukh.copyclose.features.order.list.domain.UpdateOrderStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderListViewModel(
    private val getOrderListUseCase: GetOrderListUseCase,
    private val updateOrderStateUseCase: UpdateOrderStateUseCase,
    private val reportUseCase: ReportUseCase,
) : ViewModel(), OrderListActions {
    private var _state by mutableStateOf<OrderListUIState>(OrderListUIState.Loading)
    val state: OrderListUIState get() = _state

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val ordersResult = (getOrderListUseCase())) {
                is Result.Error -> _state = OrderListUIState.Error
                is Result.Success -> {
                    val groupedOrders = groupResult(ordersResult.data)
                    (_state as? OrderLoadedStateMutable)?.apply {
                        ordersMutable.clear()
                        ordersMutable.addAll(groupedOrders)
                    } ?: run {
                        _state = OrderLoadedStateMutable(initialState = groupedOrders)
                    }
                }
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            _state = OrderListUIState.Loading
            getOrders()
        }
    }

    override fun accept(orderID: String) {
        updateState(orderID, OrderState.STATE_ACCEPTED)
    }

    override fun reject(orderID: String) {
        updateState(orderID, OrderState.STATE_REJECTED)
    }

    override fun finish(orderID: String) {
        updateState(orderID, OrderState.STATE_COMPLETED)
    }

    override fun openReport(orderUI: OrderUI) {
        (_state as? OrderLoadedStateMutable)?._dialogState = ReportingMutable("", orderUI)
    }

    override fun report() {
        viewModelScope.launch(Dispatchers.IO) {
            ((_state as? OrderLoadedStateMutable)?._dialogState as? ReportingMutable)?.apply {
                sending = true
                val result = reportUseCase(orderUI.orderID, message)
                when (result) {
                    is Result.Error -> _state = OrderListUIState.Error
                    is Result.Success -> {
                        fetchOrders()
                        sending = false
                    }
                }
            }
        }.invokeOnCompletion {
            (_state as? OrderLoadedStateMutable)?._dialogState = OrderListDialogState.None
        }
    }

    override fun setDialogMessage(message: String) {
        ((_state as? OrderLoadedStateMutable)?._dialogState as? ReportingMutable)?.apply {
            this.message = message
        }
    }

    private fun updateState(orderId: String, orderState: OrderState) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateResult = updateOrderStateUseCase(
                orderID = orderId,
                state = orderState
            )
            when (updateResult) {
                is Result.Error -> {}
                is Result.Success -> fetchOrders()
            }
        }
    }

    override fun info(orderUI: OrderUI) {
        (_state as? OrderLoadedStateMutable)?._dialogState = OrderListDialogState.OrderInfo(orderUI)
    }

    override fun dismissDialog() {
        (_state as? OrderLoadedStateMutable)?._dialogState = OrderListDialogState.None
    }

    private suspend fun groupResult(orderResult: Pair<List<OrderDTO>, List<OrderDTO>>) =
        orderResult.first.map { dto ->
            OrderUI(
                orderID = dto.orderID,
                name = dto.name,
                icon = withContext(Dispatchers.Default) {
                    UiUtils.bytesToImage(dto.icon)
                },
                id = dto.id,
                totalPrice = dto.services.sumOf { it.price * it.amount },
                serviceList = dto.services,
                addressName = dto.addressName,
                comment = dto.comment,
                attachments = dto.attachments,
                acceptable = false,
                state = dto.state,
                reported = dto.reported
            )
        }.toMutableList().apply {
            addAll(
                orderResult.second.map { dto ->
                    OrderUI(
                        orderID = dto.orderID,
                        name = dto.name,
                        icon = withContext(Dispatchers.Default) {
                            UiUtils.bytesToImage(dto.icon)
                        },
                        id = dto.id,
                        totalPrice = dto.services.sumOf { it.price * it.amount },
                        serviceList = dto.services,
                        addressName = dto.addressName,
                        comment = dto.comment,
                        attachments = dto.attachments,
                        acceptable = true,
                        state = dto.state,
                        reported = dto.reported
                    )
                }
            )
        }.groupBy {
            it.state
        }.flatMap { it.value }
}

sealed interface OrderListDialogState {
    data object None : OrderListDialogState
    class OrderInfo(val orderUI: OrderUI) : OrderListDialogState
    interface Reporting : OrderListDialogState {
        val orderUI: OrderUI
        val sending: Boolean
        val message: String
    }
}

private class ReportingMutable(
    message: String,
    override val orderUI: OrderUI
) : OrderListDialogState.Reporting {
    override var message by mutableStateOf(message)
    override var sending by mutableStateOf(false)
}

sealed interface OrderListUIState {
    data object Loading : OrderListUIState
    data object Error : OrderListUIState
    interface OrderLoadedSate : OrderListUIState {
        val dialogState: OrderListDialogState
        val orders: List<OrderUI>
    }
}

@Stable
private class OrderLoadedStateMutable(
    initialState: List<OrderUI> = emptyList(),
    initialDialogState: OrderListDialogState = OrderListDialogState.None
) : OrderListUIState.OrderLoadedSate {
    var _dialogState by mutableStateOf<OrderListDialogState>(initialDialogState)
    override val dialogState get() = _dialogState

    val ordersMutable = initialState.toMutableStateList()
    override val orders: List<OrderUI> = ordersMutable
}

@Immutable
data class OrderUI(
    val orderID: String,
    val id: String,
    val name: String,
    val icon: ImageBitmap?,
    val addressName: String,
    val comment: String,
    val totalPrice: Int,
    val serviceList: List<Service>,
    val attachments: List<Attachment>,
    val acceptable: Boolean,
    val state: OrderState,
    val reported: Boolean
)