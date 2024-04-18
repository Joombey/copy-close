package dev.farukh.copyclose.features.map.data.source

import dev.farukh.copyclose.core.AuthError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asNetworkError
import dev.farukh.copyclose.core.utils.extensions.asUnknownError
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.network.services.copyClose.file.FileService
import dev.farukh.network.services.copyClose.map.MapService
import dev.farukh.network.services.copyClose.map.response.SellerInfoResponse
import dev.farukh.network.utils.RequestResult

class RemoteSellersDataSource(
    private val mapService: MapService,
    private val fileService: FileService,
) {
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

    private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
        return if (code == 401) {
            Result.Error(AuthError.AuthTokenError)
        } else {
            asUnknownError()
        }
    }
}

