package dev.farukh.network.services.copyClose.map.response

import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SellerInfoResponse(
    @SerialName("user_id")
    val userID: String,
    @SerialName("name")
    val name: String,
    @SerialName("user_image")
    val imageID: String,
    @SerialName("role")
    val role: RoleCore,
    @SerialName("address")
    val address: AddressCore
)