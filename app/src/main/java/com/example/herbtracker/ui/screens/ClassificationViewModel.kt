package com.example.herbtracker.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbtracker.model.PhotoItemUiState
import com.example.herbtracker.ui.getWikiDescription
import com.example.herbtracker.ui.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface LoadedUiState {
    data class Success(val photos: String) : LoadedUiState
    object Error : LoadedUiState
    object Loading : LoadedUiState
}

sealed interface ModelVersionState {
    object MobileNetV2 : ModelVersionState
    object DenseNet121 : ModelVersionState
}

@HiltViewModel
class ClassificationViewModel @Inject constructor (): ViewModel() {
    var loadedUiState: LoadedUiState by mutableStateOf(LoadedUiState.Loading)
        private set

    var modelVersionState: ModelVersionState by mutableStateOf(ModelVersionState.DenseNet121)
        private set

    fun retrieveNetData(homeViewModel: HomeViewModel, photoUiState: PhotoItemUiState) {
        viewModelScope.launch{
            loadedUiState = LoadedUiState.Loading
            loadedUiState = try {
                homeViewModel.updateDescription(
                    getWikiDescription(
                        photoUiState.imageName,
                        homeViewModel
                    )
                )
                LoadedUiState.Success("")
            } catch (e: IOException) {
                LoadedUiState.Error
            } catch (e: HttpException) {
                LoadedUiState.Error
            }
        }


    }

    fun updateLoadState(state: LoadedUiState){
        loadedUiState = state
    }

    fun updateModelVersionState(modelVersion: ModelVersionState) {
        modelVersionState = modelVersion
    }
}