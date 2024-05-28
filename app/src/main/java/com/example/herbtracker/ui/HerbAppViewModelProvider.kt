package com.example.herbtracker.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.herbtracker.HerbTrackerApplication
import com.example.herbtracker.ui.home.HomeViewModel
import com.example.herbtracker.ui.screens.MapViewModel

object HerbAppViewModelProvider{
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(herbTrackerApplication().container.networkRepository)
        }
        initializer {
            MapViewModel(herbTrackerApplication().container.herbPointRepository)
        }
    }
}

fun CreationExtras.herbTrackerApplication(): HerbTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HerbTrackerApplication)