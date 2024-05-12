package dev.farukh.copyclose.features.order_creation.ui

import android.net.Uri

interface OrderCreationActions {
    fun create()
    fun addAmount(index: Int)
    fun removeAmount(index: Int)
    fun changeComment(comment: String)
    fun attachFile(uri: Uri)
    fun detachFile(index: Int)
}