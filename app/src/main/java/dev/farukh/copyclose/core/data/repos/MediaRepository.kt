package dev.farukh.copyclose.core.data.repos

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository(private val context: Context) {
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

    suspend fun bytesFromUri(uri: Uri) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)
    }

    suspend fun bytesFromUri(uriString: String) = bytesFromUri(Uri.parse(uriString))
    fun getMediaName(image: Uri): String? {
        return context.contentResolver.query(
            image,
            null,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            while (!cursor.moveToNext()) {
                return cursor.getString(nameIndex)
            }
            return null
        }
    }
}