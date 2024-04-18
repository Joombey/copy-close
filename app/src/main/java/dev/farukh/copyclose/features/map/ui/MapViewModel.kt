package dev.farukh.copyclose.features.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.farukh.copyclose.features.map.data.dto.SellerDTO
import dev.farukh.copyclose.features.map.data.repos.SellerRepository
import dev.farukh.copyclose.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val sellerRepository: SellerRepository
): ViewModel() {
    private val _sellersFlow = MutableStateFlow<List<Pair<SellerDTO, ByteArray?>>>(emptyList())
    val sellersFlow = _sellersFlow.asStateFlow()

    fun getSellers() = viewModelScope.launch(Dispatchers.IO) {
        when(val sellersResult = sellerRepository.getSellers()) {
            is Result.Error -> {}
            is Result.Success -> _sellersFlow.update { sellersResult.data }
        }
    }
}

