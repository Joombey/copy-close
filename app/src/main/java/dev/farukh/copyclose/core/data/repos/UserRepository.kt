package dev.farukh.copyclose.core.data.repos

import android.net.Uri
import db.AddressEntity
import db.RoleEntity
import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.core.data.dto.UserInfoDTO
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.core.data.source.UserRemoteDataSource
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import dev.farukh.network.core.ServiceCore
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
            val activeUser = localDataSource.getActiveUser()!!
            return when (val userInfoResult = remoteDataSource.getUserInfoV2(userID, activeUser.id, activeUser.authToken!!)) {
                is Result.Error -> userInfoResult
                is Result.Success -> userInfoResult.data.toDto()
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
                        services = services,
                        addressCore = address
                    )
                )
        }

    suspend fun getRole(id: Int) = localDataSource.getRole(id)
    suspend fun getActiveUser() = localDataSource.getActiveUser()
    suspend fun editProfile(
        idsToDelete: MutableList<String>,
        services: List<ServiceCore>,
        name: String,
        newImageUri: Uri?
    ): Result<Unit, AppError> {
        val userDTO = localDataSource.getActiveUser()!!.let { activeUser ->
            UserDTO(
                id = activeUser.id,
                roleID = activeUser.roleID.toInt(),
                addressID = activeUser.addressID,
                name = name,
                authToken = activeUser.authToken!!,
                icon = activeUser.icon,
                iconUrl = activeUser.iconID
            )
        }
        val activeUserRole = localDataSource.getRole(userDTO.roleID).toCore()
        val activeUserAddress = localDataSource.getAddressByID(userDTO.addressID).toCore()

        val editProfileResult = remoteDataSource.editProfile(
            userID = userDTO.id,
            authToken = userDTO.authToken,
            idsToDelete = idsToDelete,
            services = services,
            name = name,
            newImageUri = newImageUri
        )
        return when (editProfileResult) {
            is Result.Error -> editProfileResult
            is Result.Success -> {
                try {
                    localDataSource.createOrUpdateUser(
                        role = activeUserRole,
                        user = userDTO,
                        address = activeUserAddress
                    )
                    editProfileResult
                } catch (e: Exception) {
                    Result.Error(LocalError.NoActiveUser)
                }
            }
        }
    }

    private fun RoleEntity.toCore() = RoleCore(
        id = id.toInt(),
        canBan = canBan > 0,
        canBuy = canBuy > 0,
        canSell = canSell > 0
    )

    private fun AddressEntity.toCore() = AddressCore(
        id = id,
        lat = lat,
        lon = lon,
        addressName = addressName,
    )
}