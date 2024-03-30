package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoObject(
    @SerialName("Point")
    val point: Point,
//    val boundedBy: BoundedBy,
//    val description: String,
    val metaDataProperty: MetaDataProperty,
//    val name: String
)