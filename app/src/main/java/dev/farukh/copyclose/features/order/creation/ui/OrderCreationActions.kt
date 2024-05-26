package dev.farukh.copyclose.features.order.creation.ui

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Job

@Immutable
interface OrderCreationActions {
    fun create(): Job
    fun addAmount(index: Int)
    fun removeAmount(index: Int)
    fun changeComment(comment: String)
    fun attachFile(uri: Uri)
    fun detachFile(index: Int)
    fun creationInfoConfirmed()
}