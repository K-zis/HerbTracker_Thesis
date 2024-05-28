package com.example.herbtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HerbPointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: HerbPoint)

    @Update
    suspend fun update(item: HerbPoint)

    @Delete
    suspend fun delete(item: HerbPoint)

    @Query("SELECT * FROM herb_points ORDER BY id ASC")
    fun getAllItems(): Flow<List<HerbPoint>>
}