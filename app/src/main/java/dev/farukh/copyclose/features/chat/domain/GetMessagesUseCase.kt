package dev.farukh.copyclose.features.chat.domain

import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.data.dto.UserInfoDTO
import dev.farukh.copyclose.core.data.repos.UserRepository
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.features.chat.data.dto.ChatDTO
import dev.farukh.copyclose.features.chat.data.repos.ChatRepository
import kotlinx.coroutines.coroutineScope

class GetMessagesUseCase(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(orderID: String, userID: String): Result<ChatDTO, AppError> = coroutineScope {
        return@coroutineScope when (val chatResult = chatRepository.getMessages(orderID)) {
            is Result.Error -> Result.Error(LocalError.NoActiveUser)
            is Result.Success -> {
                val userIcon = getUserImage(userID) ?: return@coroutineScope Result.Error(LocalError.NoActiveUser)
                val userIconMap: HashMap<String, ByteArray> = hashMapOf(userID to userIcon)

                chatResult.data.firstOrNull { response ->
                    response.userId != userID
                }?.let { response ->
                    userIconMap[response.userId] = getUserImage(response.userId) ?: return@let
                }

                Result.Success(
                    ChatDTO(
                        userIconMap = userIconMap,
                        messages = chatResult.data
                    )
                )
            }
        }
    }

    private suspend fun getUserImage(userId: String) =
        (userRepository.getUserData(userId) as? Result.Success<UserInfoDTO>)?.data?.imageData
}