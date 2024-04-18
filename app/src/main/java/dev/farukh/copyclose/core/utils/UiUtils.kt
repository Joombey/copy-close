package dev.farukh.copyclose.core.utils

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.IOException

object UiUtils {
    fun bytesToImage(imageRaw: ByteArray): ImageBitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val src = ImageDecoder.createSource(imageRaw)
            try {
                ImageDecoder.decodeBitmap(src).asImageBitmap()
            } catch (_: IOException) {
                null
            }
        } else {
            BitmapFactory.decodeByteArray(imageRaw, 0, imageRaw.size)?.asImageBitmap()
        }
    }

    val arrangementDefault = 16.dp
    val contentPaddingDefault = PaddingValues(vertical = 8.dp)
    val containerPaddingDefault = PaddingValues(8.dp)
    val borderWidthDefault = 4.dp
    val roundShapeDefault = RoundedCornerShape(12.dp)
}