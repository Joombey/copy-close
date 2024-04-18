package dev.farukh.copyclose.features.map.data.repos

import dev.farukh.copyclose.core.AppError
import dev.farukh.copyclose.core.LocalError
import dev.farukh.copyclose.core.data.source.UserLocalDataSource
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.copyclose.features.map.data.source.RemoteSellersDataSource
import dev.farukh.copyclose.utils.Result

class SellerRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteSellerDataSource: RemoteSellersDataSource,
) {
    suspend fun getSellers(): Result<List<Pair<SellerDTO, ByteArray?>>, AppError> {
        val activeUser = localDataSource.getActiveUser() ?: return Result.Error(LocalError.NoActiveUser)
        return remoteSellerDataSource.getSellers(activeUser.id, activeUser.authToken!!)
    }
}

