package dev.farukh.network.services.copyClose.info

import dev.farukh.network.services.copyClose.info.response.OrderListResponse
import dev.farukh.network.services.copyClose.info.response.UserInfoResponse
import dev.farukh.network.utils.RequestResult
import dev.farukh.network.utils.commonGet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.url
import kotlinx.serialization.json.Json

interface InfoService {
    suspend fun getUserInfo(login: String, authToken: String): RequestResult<UserInfoResponse>
    suspend fun getUserInfoV2(infoUserID: String, login: String, authToken: String): RequestResult<UserInfoResponse>
    suspend fun getOrderInfoFor(userID: String, authToken: String): RequestResult<OrderListResponse>
}

internal class InfoServiceImpl(
    private val client: HttpClient,
    private val json: Json,
): InfoService {
    override suspend fun getUserInfo(login: String, authToken: String) = client.commonGet(
        onResponse = { body<UserInfoResponse>() },
        config = {
            url {
                url("user/$login")
                parameters["authToken"] = authToken
            }
        }
    )

    override suspend fun getUserInfoV2(infoUserID: String, login: String, authToken: String) = client.commonGet(
        onResponse = { body<UserInfoResponse>() },
        config = {
            url {
                url("user/$infoUserID")
                parameters["authToken"] = authToken
                parameters["userID"] = login
            }
        }
    )

    override suspend fun getOrderInfoFor(userID: String, authToken: String) = client.commonGet(
        onResponse = { body<OrderListResponse>() },
        config = {
            url {
                url("order-list/$userID")
                parameters["auth_token"] = authToken
            }
        }
    )
}