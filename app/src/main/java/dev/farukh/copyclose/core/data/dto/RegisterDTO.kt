package dev.farukh.copyclose.core.data.dto

import android.net.Uri
import dev.farukh.copyclose.core.data.model.Address

class RegisterDTO(
    val login: String,
    val password: String,
    val name: String,
    val address: Address,
    val image: Uri,
)