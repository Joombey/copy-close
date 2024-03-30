package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Locality(
    @SerialName("LocalityName")
    val localityName: String,
    @SerialName("Thoroughfare")
    val thoroughfare: Thoroughfare
)