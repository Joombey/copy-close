package dev.farukh.copyclose.core.data.source

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.ResourceError
import dev.farukh.copyclose.core.utils.MediaInserter
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asNetworkError
import dev.farukh.copyclose.core.utils.extensions.asUnknownError
import dev.farukh.network.services.copyClose.file.FileService
import dev.farukh.network.services.copyClose.info.InfoService
import dev.farukh.network.utils.RequestResult

class UserRemoteDataSource(
    private val infoService: InfoService,
    private val fileService: FileService,
    private val mediaInserter: MediaInserter,
) {
    suspend fun getUserInfo(login: String, authToken: String) = infoService.getUserInfo(login, authToken)
    suspend fun getUserImage(imageID: String): Result<String, NetworkError> {
        val uri = mediaInserter.createMedia("image/jpeg", imageID)
        return mediaInserter.getMediaOutStream(uri!!)!!.use {
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
}

private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
    return when (code) {
        404 -> Result.Error(ResourceError.NotFoundError)
        else -> asUnknownError()
    }
}