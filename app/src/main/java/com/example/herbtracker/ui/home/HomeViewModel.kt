package com.example.herbtracker.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbtracker.di.NetworkProvider
import com.example.herbtracker.di.NetworkRepository
import com.example.herbtracker.model.PhotoItemUiState
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException

sealed interface NetUiState {
    data class Success(val loaded: Boolean) : NetUiState
    object Error : NetUiState
    object Loading : NetUiState
}
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoItemUiState())
    val uiState: StateFlow<PhotoItemUiState> = _uiState.asStateFlow()
    var netUiState: NetUiState by mutableStateOf(NetUiState.Loading)
        private set


    suspend fun getWikiRequest(herbalName: String): JsonObject {
        return networkRepository.getWikiDescriptionRequest(herbalName)
    }

    fun updateFromGallery(image: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                bitmap = image,
                canNavigateToMap = false

            )
        }
    }

    fun updateFromCamera(image: Bitmap) {
        _uiState.update { currentState ->
            currentState.copy(
                bitmap = image,
                canNavigateToMap = true
            )
        }
    }

    fun updateNameAndProbability(imageName: String, score: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                imageName = imageName,
                probability = score

            )
        }
    }

    fun updateDescription(imageDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(
                imageDescription = imageDescription

            )
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                latitude = lat,
                longitude = lon
            )
        }
    }

    fun resetState() {
        _uiState.update { PhotoItemUiState() }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        viewModelScope.launch {
            netUiState = try {
                val locationResult = fusedLocationProviderClient.lastLocation
                var success = false
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateLocation(task.result.latitude, task.result.longitude)
                        success = true
                    }
                }
                delay(2_000L)
                if (success) {
                    NetUiState.Success(true)
                } else {
                    NetUiState.Error
                }
            } catch (e: SecurityException) {
                NetUiState.Error
            } catch (e: IOException) {
                NetUiState.Error
            } catch (e: HttpException) {
                NetUiState.Error
            }
        }

    }

    fun updateState(state: NetUiState){
        netUiState = state
    }

    fun updateCanNavigateToMap(canNavigate: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                canNavigateToMap = canNavigate
            )
        }
    }
}

