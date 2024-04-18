package dev.farukh.network.services.copyClose.map

import dev.farukh.network.services.copyClose.map.response.SellerInfoResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.url

interface MapService {
    suspend fun getSellers(
        userID: String,
        authToken: String
    ): RequestResult<List<SellerInfoResponse>>
}

internal class MapServiceImpl(
    private val client: HttpClient
) : MapService {
    override suspend fun getSellers(
        userID: String,
        authToken: String
    ): RequestResult<List<SellerInfoResponse>> = client.commonGet(
        onResponse = { body<List<SellerInfoResponse>>() },
        config = {
            url {
                url("sellers")
                parameters["authToken"] = authToken
                parameters["userID"] = userID
            }
        }
    )
}