package dev.farukh.copyclose.features.register.data.dto

import android.net.Uri
import dev.farukh.copyclose.features.register.data.model.Address

class RegisterDTO(
    val login: String,
    val password: String,
    val name: String,
    val address: Address,
    val image: Uri,
    val isSeller: Boolean,
    val devKey: String?,
)