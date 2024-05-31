package dev.farukh.copyclose.features.admin.data.repos

import dev.farukh.copyclose.core.utils.extensions.asResult
import dev.farukh.network.services.copyClose.admin.AdminService
import dev.farukh.network.services.copyClose.admin.request.BlockRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class AdminRepository(
    private val adminService: AdminService
) {
    fun getTriggerFlow(userId: String, authToken: String) =
        adminService.getUpdates(userId, authToken).flowOn(Dispatchers.IO)

    suspend fun getBlockList(userId: String, authToken: String) =
        adminService.getBlockList(userId, authToken).asResult()

    suspend fun block(
        userID: String,
        authToken: String,
        userBlockId: String,
        reportID: String,
        block: Boolean
    ) {
        adminService.block(
            request = BlockRequest(
                userID = userID,
                authToken = authToken,
                userBlockId = userBlockId,
                reportID = reportID
            ),
            block = block
        ).asResult()
    }
}