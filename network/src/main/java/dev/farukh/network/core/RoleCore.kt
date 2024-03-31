package dev.farukh.network.core

import kotlinx.serialization.Serializable

@Serializable
class RoleCore(
    val id: Int,
    val canBuy: Boolean,
    val canSell: Boolean,
    val canBan: Boolean
)