package dev.farukh.copyclose.core.data.repos

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.ResourceError
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.core.data.source.UserRemoteDataSource
import dev.farukh.copyclose.utils.Result
import dev.farukh.copyclose.utils.extensions.asNetworkError
import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import dev.farukh.network.services.copyClose.info.response.UserInfoResponse
import dev.farukh.network.utils.RequestResult

class UserRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) {
    val activeUser = localDataSource.activeUser
    suspend fun createUser(
        role: RoleCore,
        user: UserDTO,
        address: AddressCore,
    ) = localDataSource.createOrUpdateUser(role, user, address)

    suspend fun updateUserData(login: String, authToken: String): Result<String, NetworkError> {
        remoteDataSource.getUserInfo(login, authToken)
        return when (val infoResult = remoteDataSource.getUserInfo(login, authToken)) {
            is RequestResult.ClientError -> infoResult.asNetworkError()
            is RequestResult.ServerError -> infoResult.asNetworkError()
            is RequestResult.HostError -> infoResult.asNetworkError()
            is RequestResult.TimeoutError -> infoResult.asNetworkError()
            is RequestResult.Unknown -> infoResult.asNetworkError()

            is RequestResult.Success -> updateLocalInfo(infoResult.data)
        }
    }

    private suspend fun updateLocalInfo(info: UserInfoResponse): Result<String, NetworkError> {
        val exists = localDataSource.userExists(info.userID)
        val imageValid = localDataSource.checkImageValid(info.userID, info.imageID)

        return if (exists && imageValid) {
            Result.Success(info.userID)
        } else when (val uriResult = remoteDataSource.getUserImage(info.imageID)) {
            is Result.Error -> uriResult
            is Result.Success -> {
                localDataSource.createOrUpdateUser(
                    role = info.role,
                    address = info.address,
                    user = UserDTO(
                        id = info.userID,
                        login = info.login,
                        roleID = info.role.id,
                        addressID = info.address.id!!,
                        name = info.name,
                        authToken = info.authToken,
                        icon = uriResult.data,
                        iconUrl = info.imageID
                    )
                )
                Result.Success(info.userID)
            }
        }
    }

    suspend fun makeUserActive(userID: String) {
        localDataSource.makeUserActive(userID = userID)
    }
}

private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(
        when (code) {
            404 -> ResourceError.NotFoundError
            else -> NetworkError.UnknownError(Exception("$code $errorMessage"))
        }
    )
}