package dev.farukh.copyclose.core.data.repos

import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.core.data.models.UserInfoDTO
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.core.data.source.UserRemoteDataSource
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import dev.farukh.network.services.copyClose.info.response.UserInfoResponse

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
        return when (val infoResult = remoteDataSource.getUserInfo(login, authToken)) {
            is Result.Error -> infoResult
            is Result.Success -> updateLocalInfo(infoResult.data)
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
                        roleID = info.role.id,
                        addressID = info.address.id!!,
                        name = info.name,
                        authToken = info.authToken,
                        icon = uriResult.data,
                        iconUrl = info.imageID,
                    )
                )
                Result.Success(info.userID)
            }
        }
    }

    suspend fun makeUserActive(userID: String) {
        localDataSource.makeUserActive(userID = userID)
    }

    suspend fun makeUserInActive(userID: String) {
        localDataSource.makeUserInActive(userID)
    }

    suspend fun getSellers(): Result<List<SellerDTO>, AppError> {
        val activeUser =
            localDataSource.getActiveUser() ?: return Result.Error(LocalError.NoActiveUser)
        return remoteDataSource.getSellers(activeUser.id, activeUser.authToken!!)
    }

    suspend fun getUserData(userID: String): Result<UserInfoDTO, AppError> {
        val localUser = localDataSource.getUserByID(userID)
        return if (localUser == null) {
            val activeUser = localDataSource.getActiveUser()!!
            when (val userInfoResult = remoteDataSource.getUserInfoV2(userID, activeUser.id, activeUser.authToken!!)) {
                is Result.Error -> userInfoResult
                is Result.Success -> userInfoResult.data.toDto()
            }
        } else {
            when (val userImageResult = remoteDataSource.getUserImageRaw(localUser.iconID)) {
                is Result.Error -> userImageResult
                is Result.Success -> Result.Success(
                    UserInfoDTO(
                        userID = localUser.id,
                        name = localUser.name,
                        imageData = userImageResult.data,
                        isSeller = localDataSource.getRole(localUser.roleID).canSell == 1L,
                        categories = emptyList()
                    )
                )
            }
        }
    }

    private suspend fun UserInfoResponse.toDto(): Result<UserInfoDTO, NetworkError> =
        when (val imageRawResult = remoteDataSource.getUserImageRaw(imageID)) {
            is Result.Error -> imageRawResult
            is Result.Success ->
                Result.Success(
                    UserInfoDTO(
                        userID = userID,
                        name = name,
                        imageData = imageRawResult.data,
                        isSeller = role.canSell,
                        categories = emptyList()
                    )
                )
        }

    suspend fun getActiveUser() = localDataSource.getActiveUser()
}