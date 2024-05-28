package com.example.herbtracker.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.herbtracker.ui.screens.HerbItemDetails

@Entity(tableName = "herb_points")
data class HerbPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,

)
