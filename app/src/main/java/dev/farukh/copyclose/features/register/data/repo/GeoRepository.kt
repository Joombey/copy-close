package dev.farukh.copyclose.features.register.data.repo

import dev.farukh.copyclose.core.data.model.Address
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
    suspend fun query(q: String): RequestResult<List<Address>> = coroutineScope {
        when (val suggesterResult = yandexGeoSuggesterService.query(q)) {
            is RequestResult.ClientError -> suggesterResult
            is RequestResult.ServerInternalError -> suggesterResult
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

                RequestResult.Success(mappedResult)
            }
        }
    }
}

private fun FeatureMember.toAddress(): Address {
    val lonLatSplit = geoObject.point.pos.split(" ")
    return Address(
        addressName = geoObject.metaDataProperty.geocoderMetaData.address.formatted,
        lat = lonLatSplit[1].toDouble(),
        lon = lonLatSplit[0].toDouble()
    )
}
