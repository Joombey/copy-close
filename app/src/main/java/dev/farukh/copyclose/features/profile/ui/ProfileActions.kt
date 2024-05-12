package dev.farukh.copyclose.features.profile.ui

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Job

@Immutable
interface ProfileActions {
    fun addService()
    fun startEdit()
    fun saveChanges(): Job
    fun setIcon(newIcon: Uri): Job
    fun setTitle(index: Int, newTitle: String)
    fun setPrice(index: Int, newPrice: Int)
    fun removeServiceAt(index: Int)
    fun setName(newName: String)
}