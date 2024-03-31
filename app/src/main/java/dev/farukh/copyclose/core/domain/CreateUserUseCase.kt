package dev.farukh.copyclose.core.domain

import dev.farukh.copyclose.core.data.dto.RegisterDTO
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.core.data.repos.AuthRepository
import dev.farukh.copyclose.core.data.repos.MediaRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.network.core.AddressCore
import dev.farukh.network.services.copyClose.authService.response.RegisterResponse
import dev.farukh.network.utils.RequestResult

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        registerDTO: RegisterDTO
    ): RequestResult<Boolean> {
        val registerResult = authRepository.register(
            login = registerDTO.login,
            name = registerDTO.name,
            password = registerDTO.password,
            address = registerDTO.address,
            image = mediaRepository.bytesFromUri(registerDTO.image)!!.readBytes(),
        )
        return when (registerResult) {
            is RequestResult.ClientError -> registerResult
            is RequestResult.ServerInternalError -> registerResult
            is RequestResult.Success -> RequestResult.Success(
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
            iconUrl = response.imageUrl,
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