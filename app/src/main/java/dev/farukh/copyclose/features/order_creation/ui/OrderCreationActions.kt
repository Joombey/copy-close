package dev.farukh.copyclose.features.order_creation.ui

import android.net.Uri
import kotlinx.coroutines.Job

interface OrderCreationActions {
    fun create(): Job
    fun addAmount(index: Int)
    fun removeAmount(index: Int)
    fun changeComment(comment: String)
    fun attachFile(uri: Uri)
    fun detachFile(index: Int)
    fun creationInfoConfirmed()
}