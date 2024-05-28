package dev.farukh.copyclose.features.order.list.domain

import android.util.Log
import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.data.dto.UserInfoDTO
import dev.farukh.copyclose.core.data.repos.OrderRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.order.list.data.dto.Attachment
import dev.farukh.copyclose.features.order.list.data.dto.OrderDTO
import dev.farukh.copyclose.features.order.list.data.dto.OrderState
import dev.farukh.copyclose.features.order.list.data.dto.Service
import dev.farukh.network.services.copyClose.info.response.OrderInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetOrderListUseCase(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Pair<List<OrderDTO>, List<OrderDTO>>, AppError> =
        coroutineScope {
            val activeUser = userRepository.getActiveUser() ?: return@coroutineScope Result.Error(
                LocalError.NoActiveUser
            )

            val userID = activeUser.id
            val authToken = activeUser.authToken ?: return@coroutineScope Result.Error(
                LocalError.NoActiveUser
            )

            when (val orderListResult = orderRepository.getOrdersFor(userID, authToken)) {
                is Result.Error -> orderListResult
                is Result.Success -> {
                    val myOrderLoadJob = async {
                        loadOrders(
                            orders = orderListResult.data.myOrders,
                            loadUserData = false
                        )
                    }
                    val ordersToMeJob = async {
                        loadOrders(
                            orders = orderListResult.data.toMe,
                            loadUserData = true
                        )
                    }
                    try {
                        val myOrdersResult = myOrderLoadJob.await()
                        val orderToMe = ordersToMeJob.await()
                        when(myOrdersResult) {
                            is Result.Error -> myOrdersResult
                            is Result.Success -> {
                                when(orderToMe) {
                                    is Result.Error -> orderToMe
                                    is Result.Success -> {
                                        Result.Success(
                                            (myOrdersResult.data to orderToMe.data)
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("GetOrderListUseCase", "", e)
                        Result.Error(NetworkError.UnknownError(e))
                    }
                }
            }
        }

    private suspend fun loadOrders(
        orders: List<OrderInfo>,
        loadUserData: Boolean
    ) = coroutineScope {
        if (orders.isEmpty()) {
            return@coroutineScope Result.Success(emptyList<OrderDTO>())
        }

        val orderList = orders.map { orderInfo ->
            async {
                orderInfo to userRepository.getUserData(
                    if (loadUserData) {
                        orderInfo.userId
                    }
                    else {
                        orderInfo.sellerID
                    }
                )
            }
        }.map { getImageJob ->
            getImageJob.await()
        }

        if (orderList.any { it.second is Result.Error }) {
            orderList.first { it.second is Result.Error }.second as Result.Error<AppError>
        } else {
            val orderDTOs: List<OrderDTO> = orderList.asSequence()
                .filterIsInstance<Pair<OrderInfo, Result.Success<UserInfoDTO>>>()
                .map { responseUserDataPair ->
                    OrderDTO(
                        orderID = responseUserDataPair.first.orderID,
                        name = responseUserDataPair.second.data.name,
                        addressName = responseUserDataPair.second.data.addressCore.addressName,
                        icon = responseUserDataPair.second.data.imageData,
                        services = responseUserDataPair.first.services.map { service ->
                            Service(
                                title = service.title,
                                price = service.price,
                                amount = service.amount
                            )
                        },
                        state = OrderState.entries[responseUserDataPair.first.state],
                        id = responseUserDataPair.second.data.userID,
                        comment = responseUserDataPair.first.comment,
                        attachments = responseUserDataPair.first.attachments.map { attachment ->
                            Attachment(
                                name = attachment.name,
                                id = attachment.id
                            )
                        }
                    )
                }.toList()

            Result.Success(orderDTOs)
        }
    }
}