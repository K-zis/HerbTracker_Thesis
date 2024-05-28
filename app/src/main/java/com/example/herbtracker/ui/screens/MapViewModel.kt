package com.example.herbtracker.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbtracker.data.HerbPoint
import com.example.herbtracker.di.HerbPointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MapViewModel(private val herbPointRepository: HerbPointRepository): ViewModel() {

    private var herbItemUiState by mutableStateOf(HerbItemDetails())
        private set

    fun updateUiState(itemDetails: HerbItemDetails) {
        herbItemUiState =
            HerbItemDetails(itemDetails.name,itemDetails.latitude,itemDetails.longitude)
    }
    val mapPointUiState: StateFlow<MapPointsUiState> =
        herbPointRepository.getAllItemsStream().map { MapPointsUiState(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = MapPointsUiState()
            )


    suspend fun saveItem() {
        herbPointRepository.insertItem(herbItemUiState.toHerbPoint())
    }
}

data class HerbItemDetails(
//    val id: Int = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

fun HerbItemDetails.toHerbPoint(): HerbPoint = HerbPoint(
//    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude
)

data class MapPointsUiState(val pointList: List<HerbPoint> = listOf())