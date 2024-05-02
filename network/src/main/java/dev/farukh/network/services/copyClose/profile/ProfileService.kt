package dev.farukh.network.services.copyClose.profile

import dev.farukh.network.services.copyClose.profile.request.EditProfileRequest
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonPost
import dev.farukh.network.utils.mimeString
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ProfileService {
    suspend fun editProfile(
        editProfileRequest: EditProfileRequest,
        image: ByteArray?
    ): RequestResult<Unit>
}

internal class ProfileServiceImpl(
    private val client: HttpClient,
    private val json: Json
) : ProfileService {
    override suspend fun editProfile(
        editProfileRequest: EditProfileRequest,
        image: ByteArray?
    ) = client.commonPost(
        onResponse = {},
        config = {
            url("edit-profile")
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "data",
                            value = json.encodeToString(editProfileRequest),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    ContentType.Application.Json.mimeString
                                )
                            }
                        )
                        if (image != null) {
                            append(
                                key = "image",
                                value = image,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        ContentType.Image.JPEG.mimeString
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=1"
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }
    )
}