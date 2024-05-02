package dev.farukh.copyclose.core.data.source

import android.net.Uri
import dev.farukh.copyclose.core.AuthError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.ResourceError
import dev.farukh.copyclose.core.utils.MediaManager
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asNetworkError
import dev.farukh.copyclose.core.utils.extensions.asUnknownError
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.network.core.ServiceCore
import dev.farukh.network.services.copyClose.file.FileService
import dev.farukh.network.services.copyClose.info.InfoService
import dev.farukh.network.services.copyClose.info.response.UserInfoResponse
import dev.farukh.network.services.copyClose.map.MapService
import dev.farukh.network.services.copyClose.map.response.SellerInfoResponse
import dev.farukh.network.services.copyClose.profile.ProfileService
import dev.farukh.network.services.copyClose.profile.request.EditProfileRequest
import dev.farukh.network.utils.RequestResult

class UserRemoteDataSource(
    private val infoService: InfoService,
    private val fileService: FileService,
    private val mediaManager: MediaManager,
    private val mapService: MapService,
    private val profileService: ProfileService,
) {
    suspend fun getUserInfo(login: String, authToken: String): Result<UserInfoResponse, NetworkError> {
        return when(val userInfoResult = infoService.getUserInfo(login, authToken)) {
            is RequestResult.ClientError -> userInfoResult.asNetworkError()
            is RequestResult.HostError -> userInfoResult.asNetworkError()
            is RequestResult.ServerError -> userInfoResult.asNetworkError()
            is RequestResult.TimeoutError -> userInfoResult.asNetworkError()
            is RequestResult.Unknown -> userInfoResult.asNetworkError()
            is RequestResult.Success -> Result.Success(userInfoResult.data)
        }
    }

    suspend fun getUserInfoV2(infoUserID: String, userID: String, authToken: String): Result<UserInfoResponse, NetworkError> {
        return when(val userInfoResult = infoService.getUserInfoV2(infoUserID, userID, authToken)) {
            is RequestResult.ClientError -> userInfoResult.asNetworkError()
            is RequestResult.HostError -> userInfoResult.asNetworkError()
            is RequestResult.ServerError -> userInfoResult.asNetworkError()
            is RequestResult.TimeoutError -> userInfoResult.asNetworkError()
            is RequestResult.Unknown -> userInfoResult.asNetworkError()
            is RequestResult.Success -> Result.Success(userInfoResult.data)
        }
    }


    suspend fun getUserImage(imageID: String): Result<String, NetworkError> {
        val uri = mediaManager.createMedia("image/jpeg", imageID)
        return mediaManager.getMediaOutStream(uri!!)!!.use {
            when (val result = fileService.getImage(imageID, it)) {
                is RequestResult.ClientError -> result.asNetworkError()
                is RequestResult.ServerError -> result.asNetworkError()
                is RequestResult.HostError -> result.asNetworkError()
                is RequestResult.TimeoutError -> result.asNetworkError()
                is RequestResult.Unknown -> result.asNetworkError()

                is RequestResult.Success -> Result.Success(uri.toString())
            }
        }
    }

    suspend fun getUserImageRaw(imageID: String): Result<ByteArray, NetworkError> {
        return when (val result = fileService.getImage(imageID)) {
            is RequestResult.ClientError -> result.asNetworkError()
            is RequestResult.ServerError -> result.asNetworkError()
            is RequestResult.HostError -> result.asNetworkError()
            is RequestResult.TimeoutError -> result.asNetworkError()
            is RequestResult.Unknown -> result.asNetworkError()

            is RequestResult.Success -> Result.Success(result.data)
        }
    }

    suspend fun getSellers(
        userID: String,
        authToken: String
    ): Result<List<SellerDTO>, NetworkError> {
        return when (val sellerResult = mapService.getSellers(userID, authToken)) {
            is RequestResult.ClientError -> sellerResult.asNetworkError()
            is RequestResult.HostError -> sellerResult.asNetworkError()
            is RequestResult.ServerError -> sellerResult.asNetworkError()
            is RequestResult.TimeoutError -> sellerResult.asNetworkError()
            is RequestResult.Unknown -> sellerResult.asNetworkError()

            is RequestResult.Success -> Result.Success(
                sellerResult.data
                    .map { sellerInfo ->
                        val sellerImage = fileService.getImage(sellerInfo.imageID)
                        if (sellerImage is RequestResult.Success)
                            sellerInfo.toDto().copy(
                                imageRaw = sellerImage.data
                            )
                        else
                            sellerInfo.toDto().copy(
                                imageRaw = null
                            )
                    }
            )
        }
    }

    private fun SellerInfoResponse.toDto() = SellerDTO(
        id = userID,
        addressCore = address,
        name = name,
        imageID = imageID
    )

    suspend fun editProfile(
        userID: String,
        authToken: String,
        idsToDelete: MutableList<String>,
        services: List<ServiceCore>,
        name: String,
        newImageUri: Uri?
    ): Result<Unit, NetworkError> {
        val editResult = profileService.editProfile(
            editProfileRequest = EditProfileRequest(
                userID = userID,
                authToken = authToken,
                servicesToDelete = idsToDelete,
                services = services,
                name = name
            ),
            image = newImageUri?.let { mediaManager.getMediaByUri(newImageUri) }
        )
        return when(editResult) {
            is RequestResult.ClientError -> editResult.asNetworkError()
            is RequestResult.HostError -> editResult.asNetworkError()
            is RequestResult.ServerError -> editResult.asNetworkError()
            is RequestResult.TimeoutError -> editResult.asNetworkError()
            is RequestResult.Unknown -> editResult.asNetworkError()

            is RequestResult.Success -> Result.Success(Unit)
        }
    }
}

private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
    return when (code) {
        401 -> Result.Error(AuthError.AuthTokenError)
        404 -> Result.Error(ResourceError.NotFoundError)
        else -> asUnknownError()
    }
}