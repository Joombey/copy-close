package dev.farukh.copyclose.features.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.core.utils.Result
import dev.farukh.copyclose.core.utils.UiUtils
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.copyclose.features.map.data.repos.SellerRepository
import dev.farukh.copyclose.features.map.ui.model.SellerUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint

class MapViewModel(
    private val sellerRepository: SellerRepository
): ViewModel() {
    private val _sellersFlow = MutableStateFlow<List<SellerUI>>(emptyList())
    val sellersFlow = _sellersFlow.asStateFlow()

    private val _mapUIState = MapUIStateMutable()
    val mapUIState: MapUIState get() = _mapUIState

    fun getSellers() = viewModelScope.launch(Dispatchers.IO) {
        when(val sellersResult = sellerRepository.getSellers()) {
            is Result.Error -> {}
            is Result.Success -> _sellersFlow.update { sellersResult.data.toUI() }
        }
    }

    fun setSellerLocation(seller: SellerUI) {
        _sellersFlow.update { sellerUIS ->
            sellerUIS.toMutableList().apply {
                removeIf { seller.id == it.id }
                add(seller)
            }
        }
    }

    fun setZoom(zoomLevel: Double) {
        _mapUIState.zoomLevel = zoomLevel
    }

    fun setCenter(center: IGeoPoint) {
        _mapUIState.mapCenter = center
    }
}

private class MapUIStateMutable: MapUIState {
    override var zoomLevel = 7.0
    override var mapCenter: IGeoPoint = GeoPoint(0.0, 0.0)
}

interface MapUIState {
    val zoomLevel: Double
    val mapCenter: IGeoPoint
}

private fun List<SellerDTO>.toUI() = map { it.toUI() }
private fun SellerDTO.toUI() = SellerUI(
    id = id,
    name = name,
    address = addressCore,
    imageID = imageID,
    image = imageRaw?.let { UiUtils.bytesToImage(it) }
)

