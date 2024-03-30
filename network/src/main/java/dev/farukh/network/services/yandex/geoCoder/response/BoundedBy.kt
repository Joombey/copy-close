package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoundedBy(
    @SerialName("Envelope")
    val envelope: Envelope
)