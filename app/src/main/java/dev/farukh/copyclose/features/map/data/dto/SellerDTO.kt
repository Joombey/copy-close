package dev.farukh.copyclose.features.map.data.dto

import dev.farukh.network.core.AddressCore

data class SellerDTO(
    val id: String,
    val addressCore: AddressCore,
    val name: String,
    val imageID: String,
    val imageRaw: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SellerDTO

        if (id != other.id) return false
        if (addressCore != other.addressCore) return false
        if (name != other.name) return false
        if (imageID != other.imageID) return false
        if (imageRaw != null) {
            if (other.imageRaw == null) return false
            if (!imageRaw.contentEquals(other.imageRaw)) return false
        } else if (other.imageRaw != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + addressCore.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageID.hashCode()
        result = 31 * result + (imageRaw?.contentHashCode() ?: 0)
        return result
    }
}