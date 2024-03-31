package dev.farukh.copyclose.features.register.data

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaContentRepository(private val context: Context) {
    suspend fun getImage(uri: Uri): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } else {
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            }.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}