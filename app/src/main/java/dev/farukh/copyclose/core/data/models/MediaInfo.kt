package dev.farukh.copyclose.core.data.models

import android.net.Uri

data class MediaInfo(
    val name: String,
    val size: Long,
    val mimeType: String,
    val extensions: String,
    val uri: Uri,
)