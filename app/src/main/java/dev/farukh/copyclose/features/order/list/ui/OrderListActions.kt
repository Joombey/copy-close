package dev.farukh.copyclose.features.order.list.ui

import androidx.compose.runtime.Immutable

@Immutable
interface OrderListActions {
    fun accept(orderID: String)
    fun reject(orderID: String)
    fun info(orderUI: OrderUI)
    fun dismissInfo()
    fun finish(orderID: String)
}