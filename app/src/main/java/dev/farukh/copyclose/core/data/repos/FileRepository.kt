package dev.farukh.copyclose.core.data.repos

import dev.farukh.copyclose.core.data.models.MediaInfo
import dev.farukh.copyclose.core.utils.MediaManager
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asNetworkError
import dev.farukh.copyclose.core.utils.extensions.asUnknownError
import dev.farukh.copyclose.core.utils.extensions.hasUnSuccess
import dev.farukh.network.core.UploadProgress
import dev.farukh.network.services.copyClose.file.FileService
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile

class FileRepository(
    private val fileService: FileService,
    mediaManager: MediaManager
) : MediaManager by mediaManager {
    suspend fun sendFiles(medias: List<MediaInfo>): Result<Flow<Pair<Float, List<String>>>, MediaInfo> {
        return coroutineScope {
            val sessionCreationResult = medias.map { media ->
                async { media to fileService.createUploadSession(media.name, media.size) }
            }.map { sessionCreationJob -> sessionCreationJob.await() }
            return@coroutineScope if (
                sessionCreationResult.map { it.second }.hasUnSuccess
            ) {
                Result.Error(
                    sessionCreationResult.first { mediaRequestPair ->
                        mediaRequestPair.second !is RequestResult.Success
                    }.first
                )
            } else {
                val uploadFlows = sessionCreationResult
                    .map { mediaRequestPair ->
                        mediaRequestPair.first to mediaRequestPair.second as RequestResult.Success
                    }
                    .map { mediaRequestPair ->
                        val (media, sessionResult) = mediaRequestPair
                        val sendFileFlow = fileService.sendFile(
                            name = media.name,
                            streamLauncher = { getMediaInputStream(media.uri)!! },
                            size = media.size,
                            sessionID = sessionResult.data,
                        ).map { requestResult ->
                            when (requestResult) {
                                is RequestResult.ClientError -> requestResult.asUnknownError()
                                is RequestResult.HostError -> requestResult.asNetworkError()
                                is RequestResult.ServerError -> requestResult.asNetworkError()
                                is RequestResult.TimeoutError -> requestResult.asNetworkError()
                                is RequestResult.Unknown -> requestResult.asNetworkError()

                                is RequestResult.Success -> Result.Success(requestResult.data)
                            }
                        }
                        sendFileFlow
                    }

                val progressFlow: Flow<Pair<Float, List<String>>> =
                    combine(uploadFlows) { results ->
                        if (results.any { it is Result.Error }) {
                            val errorResult = results.first { it is Result.Error } as Result.Error
                            return@combine Result.Error(errorResult.data)
                        }

                        val progressMap = results.asSequence()
                            .map { it as Result.Success<UploadProgress<String, String?>> }
                            .map { it.data }
                            .toList()

                        val sentProgress = progressMap.sumOf { it.sent }.toFloat()
                        val total = progressMap.sumOf { it.total }
                        val identifiers = progressMap.mapNotNull { it.data }

                        Result.Success((sentProgress / total) to identifiers)
                    }
                        .takeWhile { it is Result.Success }
                        .filterIsInstance<Result.Success<Pair<Float, List<String>>>>()
                        .map { it.data }

                Result.Success(progressFlow)
            }
        }
    }
}