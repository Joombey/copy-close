package dev.farukh.copyclose.core.data.repos

import dev.farukh.copyclose.core.data.model.Address
import dev.farukh.network.core.AddressCore
import dev.farukh.network.services.copyClose.authService.AuthService
import dev.farukh.network.services.copyClose.authService.request.RegisterRequest

class AuthRepository(private val authService: AuthService) {
    suspend fun register(
        login: String,
        name: String,
        password: String,
        address: Address,
        image: ByteArray,
        isSeller: Boolean,
    ) = authService.register(
        RegisterRequest(
            login = login,
            password = password,
            address = AddressCore(
                lat = address.lat,
                lon = address.lon,
                addressName = address.addressName
            ),
            name = name,
            isSeller = isSeller
        ),
        image = image,
    )
}