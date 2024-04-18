package dev.farukh.copyclose.features.map.ui.model

import androidx.compose.ui.graphics.ImageBitmap
import dev.farukh.network.core.AddressCore

class SellerUI(
    val id: String,
    val address: AddressCore,
    val name: String,
    val imageID: String,
    val image: ImageBitmap?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SellerUI
        return id == other.id &&
                address.id == other.address.id &&
                name == other.name &&
                imageID == other.imageID

    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageID.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        return result
    }
}