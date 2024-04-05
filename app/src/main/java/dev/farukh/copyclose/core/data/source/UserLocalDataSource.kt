package dev.farukh.copyclose.core.data.source

import db.CopyCloseDB
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.utils.extensions.long
import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserLocalDataSource(private val db: CopyCloseDB) {
    suspend fun createOrUpdateUser(
        role: RoleCore,
        user: UserDTO,
        address: AddressCore,
    ) {
        withContext(Dispatchers.IO) {
            db.addressQueries.createAddress(
                id = address.id!!,
                addressName = address.addressName,
                lat = address.lat,
                lon = address.lon
            )
            db.roleQueries.createRole(
                id = role.id.toLong(),
                canBuy = role.canBuy.long,
                canBan = role.canBan.long,
                canSell = role.canSell.long
            )
            db.userQueries.createUser(
                id = user.id,
                name = user.name,
                login = user.login,
                icon = user.icon,
                roleID = role.id.toLong(),
                addressID = address.id!!,
                authToken = user.authToken,
                iconID = user.iconUrl
            )
        }
    }

    suspend fun checkImageValid(userID: String, imageID: String): Boolean = withContext(Dispatchers.IO){
        db.userQueries.getIconByID(userID).executeAsOneOrNull() == null
    }

    suspend fun userExists(userID: String): Boolean = withContext(Dispatchers.IO) {
        db.userQueries.userExists(userID).executeAsOne()
    }
}