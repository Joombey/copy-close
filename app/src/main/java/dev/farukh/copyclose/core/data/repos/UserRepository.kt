package dev.farukh.copyclose.core.data.repos

import db.CopyCloseDB
import dev.farukh.copyclose.core.data.dto.UserDTO
import dev.farukh.copyclose.utils.long
import dev.farukh.network.core.AddressCore
import dev.farukh.network.core.RoleCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val db: CopyCloseDB) {
    suspend fun createUser(
        role: RoleCore,
        user: UserDTO,
        address: AddressCore,
    ) {
        withContext(Dispatchers.IO) {
            if (db.addressQueries.addressExists(address.id).executeAsOne()) {
                db.addressQueries.createAddress(
                    id = address.id,
                    addressName = address.addressName,
                    lat = address.lat,
                    lon = address.lon
                )
            }

            if (!db.roleQueries.roleExists(user.roleID.toLong()).executeAsOne()) {
                db.roleQueries.createRole(
                    id = role.id.toLong(),
                    canBuy = role.canBuy.long,
                    canBan = role.canBan.long,
                    canSell = role.canSell.long
                )
            }

            db.userQueries.createUser(
                id = user.id,
                name = user.name,
                login = user.login,
                icon = user.icon,
                roleID = role.id.toLong(),
                addressID = address.id,
                authToken = user.authToken,
                iconUrl = user.iconUrl
            )
        }
    }
}