package dev.farukh.copyclose.features.map.ui.compose

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.farukh.copyclose.core.utils.extensions.toast
import dev.farukh.copyclose.features.map.ui.model.SellerUI
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

fun markerView(
    sellerUI: SellerUI,
    context: Context,
    onLongClick: () -> Unit = {}
) = ComposeView(context = context).apply {
    layoutParams = MapView.LayoutParams(
        MapView.LayoutParams.WRAP_CONTENT,
        MapView.LayoutParams.WRAP_CONTENT,
        GeoPoint(sellerUI.address.lat, sellerUI.address.lon),
        MapView.LayoutParams.CENTER,
        0,
        0
    )

    setContent {
        MapUserImage(sellerUI = sellerUI, onLongClick = onLongClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MapUserImage(
    sellerUI: SellerUI,
    onLongClick: () -> Unit,
) {
    val context = LocalContext.current
    val userImageModifier = Modifier
        .clip(CircleShape)
        .size(30.dp)
        .combinedClickable(
            onClick = { context.toast(sellerUI.name) },
            onLongClick = onLongClick
        )
    if (sellerUI.image == null) {
        Image(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = userImageModifier
        )
    } else {
        Image(
            bitmap = sellerUI.image,
            contentDescription = null,
            modifier = userImageModifier,
            contentScale = ContentScale.Crop
        )
    }
}
