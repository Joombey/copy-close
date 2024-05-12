package dev.farukh.copyclose.core.utils

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.core.utils.extensions.toast
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

    val dividerThicknessDefault: Dp = 2.dp
    val imageSizeMedium: Dp = 50.dp
    val arrangementDefault = 16.dp
    val contentPaddingDefault = PaddingValues(vertical = 8.dp)
    val containerPaddingDefault = PaddingValues(8.dp)
    val borderWidthDefault = 4.dp
    val roundShapeDefault = RoundedCornerShape(12.dp)
}

@Composable
fun Toast(@StringRes stringID: Int) {
    LocalContext.current.toast(stringID)
}

@Composable
fun CircleImage(icon: ImageBitmap, size: Dp) {
    Image(
        bitmap = icon,
        contentDescription = null,
        modifier = Modifier.clip(CircleShape).size(size),
        contentScale = ContentScale.Crop
    )
}