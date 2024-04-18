package dev.farukh.copyclose.features.register.data.repos

import dev.farukh.copyclose.core.AuthError
import dev.farukh.copyclose.core.NetworkError
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.extensions.asNetworkError
import dev.farukh.copyclose.features.register.data.model.Address
import dev.farukh.network.services.yandex.geoCoder.YandexGeoCoderService
import dev.farukh.network.services.yandex.geoCoder.response.FeatureMember
import dev.farukh.network.services.yandex.geoCoder.response.GeoCoderResponse
import dev.farukh.network.services.yandex.geoSuggester.YandexGeoSuggesterService
import dev.farukh.network.utils.RequestResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GeoRepository(
    private val yandexGeoSuggesterService: YandexGeoSuggesterService,
    private val yandexGeoCoderService: YandexGeoCoderService
) {
    suspend fun query(q: String): Result<List<Address>, NetworkError> = coroutineScope {
        when (val suggesterResult = yandexGeoSuggesterService.query(q)) {
            is RequestResult.ClientError -> suggesterResult.asNetworkError()
            is RequestResult.ServerError -> suggesterResult.asNetworkError()
            is RequestResult.HostError -> suggesterResult.asNetworkError()
            is RequestResult.TimeoutError -> suggesterResult.asNetworkError()
            is RequestResult.Unknown -> suggesterResult.asNetworkError()

            is RequestResult.Success -> {
                val mappedResult = suggesterResult.data.results
                    .map { suggest -> async { yandexGeoCoderService.withUri(suggest.uri) } }
                    .map { it.await() }
                    .asSequence()
                    .filterIsInstance<RequestResult.Success<GeoCoderResponse>>()
                    .map { geoCode ->
                        geoCode.data
                            .response
                            .geoObjectCollection
                            .featureMember.map { member ->
                                member.toAddress()
                            }
                    }
                    .flatten()
                    .toList()

                Result.Success(mappedResult)
            }
        }
    }
}

private fun RequestResult.ClientError.asNetworkError(): Result.Error<NetworkError> {
    return Result.Error(
        when (code) {
            401 -> AuthError.AuthTokenError
            else -> NetworkError.UnknownError(Exception("got unknown code: $code\nmessage: $errorMessage"))
        }
    )
}

private fun FeatureMember.toAddress(): Address {
    val lonLatSplit = geoObject.point.pos.split(" ")
    return Address(
        addressName = geoObject.metaDataProperty.geocoderMetaData.address.formatted,
        lat = lonLatSplit[1].toDouble(),
        lon = lonLatSplit[0].toDouble()
    )
}
