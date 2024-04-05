package dev.farukh.network.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RoleCore(
    @SerialName("id")
    val id: Int,
    @SerialName("can_buy")
    val canBuy: Boolean,
    @SerialName("can_sell")
    val canSell: Boolean,
    @SerialName("can_ban")
    val canBan: Boolean
)