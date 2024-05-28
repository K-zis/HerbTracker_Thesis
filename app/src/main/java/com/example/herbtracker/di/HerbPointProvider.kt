package com.example.herbtracker.di

import com.example.herbtracker.data.HerbPointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HerbPointProvider {

    @Provides
    @Singleton
    fun provideHerbMapRepository(herbPointDao: HerbPointDao): HerbPointRepository {
        return HerbofflineRepository(herbPointDao)
    }
}