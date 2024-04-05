package dev.farukh.copyclose.core.data.source

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.ResourceError
import dev.farukh.copyclose.utils.MediaInserter
import dev.farukh.copyclose.utils.Result
import dev.farukh.copyclose.utils.extensions.asNetworkError
import dev.farukh.copyclose.utils.extensions.asUnknownError
import dev.farukh.network.services.copyClose.common.CommonService
import dev.farukh.network.utils.RequestResult

class UserRemoteDataSource(
    private val api: CommonService,
    private val mediaInserter: MediaInserter,
) {
    suspend fun getUserInfo(login: String, authToken: String) = api.getUserInfo(login, authToken)
    suspend fun getUserImage(imageID: String): Result<String, NetworkError> {
        val uri = mediaInserter.createMedia("image/jpeg", imageID)
        return mediaInserter.getMediaOutStream(uri!!)!!.use {
            when (val result = api.getImage(imageID, it)) {
                is RequestResult.ClientError -> result.asNetworkError()
                is RequestResult.ServerError -> result.asNetworkError()
                is RequestResult.HostError -> result.asNetworkError()
                is RequestResult.TimeoutError -> result.asNetworkError()
                is RequestResult.Unknown -> result.asNetworkError()

                is RequestResult.Success -> Result.Success(uri.toString())
            }
        }
    }
}

private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
    return when (code) {
        404 -> Result.Error(ResourceError.NotFoundError)
        else -> asUnknownError()
    }
}