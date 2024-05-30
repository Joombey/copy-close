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
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.order.list.data.dto.Attachment
import dev.farukh.copyclose.features.order.list.data.dto.OrderDTO
import dev.farukh.copyclose.features.order.list.data.dto.OrderState
import dev.farukh.copyclose.features.order.list.data.dto.Service
import dev.farukh.copyclose.features.order.list.domain.GetOrderListUseCase
import dev.farukh.copyclose.features.order.list.domain.UpdateOrderStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderListViewModel(
    private val getOrderListUseCase: GetOrderListUseCase,
    private val updateOrderStateUseCase: UpdateOrderStateUseCase,
) : ViewModel(), OrderListActions {
    private var _state by mutableStateOf<OrderListUIState>(OrderListUIState.Loading)
    val state: OrderListUIState get() = _state

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            when (val ordersResult = (getOrderListUseCase.invoke())) {
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

    override fun accept(orderID: String) {
        updateState(orderID, OrderState.STATE_ACCEPTED)
    }

    override fun reject(orderID: String) {
        updateState(orderID, OrderState.STATE_REJECTED)
    }

    override fun finish(orderID: String) {
        updateState(orderID, OrderState.STATE_COMPLETED)
    }

    private fun updateState(orderId: String, orderState: OrderState) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateResult = updateOrderStateUseCase(
                orderID = orderId,
                state = orderState
            )
            when (updateResult) {
                is Result.Error -> {}
                is Result.Success -> getOrders()
            }
        }
    }

    override fun info(orderUI: OrderUI) {
        (_state as? OrderLoadedStateMutable)?.orderInfoOpened = orderUI
    }

    override fun dismissInfo() {
        (_state as? OrderLoadedStateMutable)?.orderInfoOpened = null
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
                state = dto.state
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
                        state = dto.state
                    )
                }
            )
        }.groupBy {
            it.state
        }.flatMap { it.value }
}

sealed interface OrderListUIState {
    data object Loading : OrderListUIState
    data object Error : OrderListUIState
    interface OrderLoadedSate : OrderListUIState {
        val orderInfoOpened: OrderUI?
        val orders: List<OrderUI>
    }
}

@Stable
private class OrderLoadedStateMutable(
    initialState: List<OrderUI> = emptyList(),
    initialOrderInfoOpened: OrderUI? = null,
) : OrderListUIState.OrderLoadedSate {
    override var orderInfoOpened: OrderUI? by mutableStateOf(initialOrderInfoOpened)

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
)