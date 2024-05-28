package com.example.herbtracker.di

import com.example.herbtracker.data.HerbPoint
import com.example.herbtracker.data.HerbPointDao
import kotlinx.coroutines.flow.Flow

class HerbofflineRepository(private val itemDao: HerbPointDao) : HerbPointRepository {
    override fun getAllItemsStream(): Flow<List<HerbPoint>> = itemDao.getAllItems()

    override suspend fun insertItem(item: HerbPoint) = itemDao.insert(item)

    override suspend fun deleteItem(item: HerbPoint) = itemDao.delete(item)

    override suspend fun updateItem(item: HerbPoint) = itemDao.update(item)
}