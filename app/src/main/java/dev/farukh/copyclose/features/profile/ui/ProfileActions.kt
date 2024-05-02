package dev.farukh.copyclose.features.profile.ui

import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Job

@Immutable
interface ProfileActions {
    fun setTitle(index: Int, newTitle: String)
    fun setPrice(index: Int, newPrice: Int)
    fun removeServiceAt(index: Int)
    fun setIcon(newIcon: Uri): Job
    fun setName(newName: String)
    fun addService()
    fun startEdit()
    fun saveChanges(): Job
}