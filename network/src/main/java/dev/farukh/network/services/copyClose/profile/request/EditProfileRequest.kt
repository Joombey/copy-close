package dev.farukh.network.services.copyClose.profile.request

import dev.farukh.network.core.ServiceCore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EditProfileRequest(
    @SerialName("id")
    val userID: String,
    @SerialName("auth_token")
    val authToken: String,
    @SerialName("name")
    val name: String,
    @SerialName("services")
    val services: List<ServiceCore>,
    @SerialName("services_to_delete")
    val servicesToDelete: List<String>
)