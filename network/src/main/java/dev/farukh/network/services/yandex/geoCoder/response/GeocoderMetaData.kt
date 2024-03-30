package dev.farukh.network.services.yandex.geoCoder.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocoderMetaData(
    @SerialName("Address")
    val address: GeoCoderAddress,
//    @SerialName("AddressDetails")
//    val addressDetails: AddressDetails,
//    val kind: String,
//    val precision: String,
//    val text: String
)