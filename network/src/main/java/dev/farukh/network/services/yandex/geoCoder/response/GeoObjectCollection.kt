package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.Serializable

@Serializable
data class GeoObjectCollection(
    val featureMember: List<FeatureMember>,
//    val metaDataProperty: MetaDataPropertyX
)