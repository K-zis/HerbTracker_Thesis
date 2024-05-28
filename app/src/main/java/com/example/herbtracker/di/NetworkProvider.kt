package com.example.herbtracker.di

import com.example.herbtracker.network.WikiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkProvider {

    @Provides
    @Singleton
    fun provideNetworkRepository(client: WikiService): NetworkRepository {
        return NetworkRepository(client)
    }


}