package dev.farukh.copyclose.features.register.data

import dev.farukh.copyclose.core.model.Address
import dev.farukh.network.requests.SignUpModel
import dev.farukh.network.responses.AddressSuggestion
import dev.farukh.network.services.AuthService
import dev.farukh.network.services.DaDataService
import dev.farukh.network.utils.RequestResult

class RegisterRepository(
    private val authService: AuthService,
    private val daDataService: DaDataService,
) {
    suspend fun query(q: String): RequestResult<List<Address>> {
        return when (val result = daDataService.getAddressSuggestion(q)) {
            RequestResult.ClientError -> RequestResult.ClientError
            RequestResult.ServerInternalError -> RequestResult.ServerInternalError
            is RequestResult.Success -> RequestResult.Success(
                data = result.data.map {
                    it.toAddress()
                }
            )
        }
    }

    suspend fun register(
        name: String,
        login: String,
        password: String,
        lat: Double,
        lon: Double,
        addressName: String
    ): RequestResult<Boolean> {
        val model = SignUpModel(
            name = name,
            login = login,
            password = password,
            lat = lat,
            lon = lon,
            address = addressName,
        )
        return authService.signUp(model)
    }
}

private fun AddressSuggestion.toAddress() = Address(
    addressName = this.result ?: "",
    lat = geoLat.toDouble(),
    lon = geoLon.toDouble()
)
