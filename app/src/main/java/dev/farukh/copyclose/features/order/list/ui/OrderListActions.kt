package dev.farukh.copyclose.features.order.list.ui

import androidx.compose.runtime.Immutable

@Immutable
interface OrderListActions {
    fun accept(orderID: String)
    fun reject(orderID: String)
    fun info(orderUI: OrderUI)
    fun dismissDialog()
    fun finish(orderID: String)
    fun openReport(orderUI: OrderUI)
    fun report()
    fun setDialogMessage(message: String)
}