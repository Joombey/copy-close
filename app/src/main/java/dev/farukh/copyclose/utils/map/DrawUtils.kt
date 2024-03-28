package dev.farukh.copyclose.utils.map

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DrawUtils {
    suspend fun bitmapFromResource(resources: Resources, img: Int): BitmapDrawable {
        val bitmap = withContext(Dispatchers.Default) {
            resources.getDrawable(img, null).toBitmap()
        }
        return BitmapDrawable(resources, bitmap)
    }
}