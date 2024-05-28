package com.example.herbtracker.model

import android.graphics.Bitmap

const val ACCEPTABLE_PROBABILITY_LIMIT = 0.2
const val IMAGE_SIZE = 224
data class PhotoItemUiState(
    val bitmap: Bitmap? = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
    val imageName: String = "",
    val imageDescription: String = "",
    val canNavigateToMap: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val probability: Double = 0.0
)
