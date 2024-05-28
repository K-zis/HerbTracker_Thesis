package com.example.herbtracker.di

import com.example.herbtracker.data.HerbPoint
import kotlinx.coroutines.flow.Flow

interface HerbPointRepository {
    fun getAllItemsStream(): Flow<List<HerbPoint>>

    suspend fun insertItem(item: HerbPoint)

    suspend fun deleteItem(item: HerbPoint)

    suspend fun updateItem(item: HerbPoint)
}

