package dev.farukh.copyclose.core.domain

import dev.farukh.copyclose.core.data.dto.RegisterDTO
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.core.data.repos.AuthRepository
import dev.farukh.copyclose.core.data.repos.MediaRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.utils.Result
import dev.farukh.network.core.AddressCore
import dev.farukh.network.services.copyClose.auth.response.RegisterResponse

class RegisterUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        registerDTO: RegisterDTO
    ): Result<Boolean, Unit> {
        val registerResult = authRepository.register(
            login = registerDTO.login,
            name = registerDTO.name,
            password = registerDTO.password,
            address = registerDTO.address,
            isSeller = registerDTO.isSeller,
            image = mediaRepository.bytesFromUri(registerDTO.image)!!.readBytes(),
        )
        return when (registerResult) {
            is Result.Error -> Result.Error(Unit)
            is Result.Success -> Result.Success(
                onRegisterSuccess(
                    response = registerResult.data,
                    registerDTO = registerDTO
                )
            )
        }
    }

    private suspend fun onRegisterSuccess(
        response: RegisterResponse,
        registerDTO: RegisterDTO,
    ): Boolean = try {
        val userDTO = UserDTO(
            id = response.userID,
            roleID = response.role.id,
            addressID = response.addressID,
            authToken = response.authToken,
            iconUrl = response.imageID,
            name = registerDTO.name,
            icon = registerDTO.image.toString(),
            login = registerDTO.login,
        )

        val addressCore = AddressCore(
            id = response.addressID,
            addressName = registerDTO.address.addressName,
            lat = registerDTO.address.lat,
            lon = registerDTO.address.lon,
        )

        userRepository.createUser(
            role = response.role,
            user = userDTO,
            address = addressCore
        )
        true
    } catch (e: Exception) {
        false
    }
}