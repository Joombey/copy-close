package dev.farukh.copyclose.features.auth.domain

import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.data.repos.AuthRepository
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result

class LoginUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(login: String, password: String): Result<String, NetworkError> {
        return when(val authResult = authRepository.logIn(login, password)) {
            is Result.Error -> authResult
            is Result.Success -> userRepository.updateUserData(login, authResult.data)
        }
    }
}