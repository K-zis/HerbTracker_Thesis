package com.example.herbtracker.di

import android.content.Context
import androidx.room.Room
import com.example.herbtracker.data.HerbDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProvider {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, HerbDatabase::class.java, "item_database.db"
    ).build()

    @Singleton
    @Provides
    fun provideHerbPointDao(database: HerbDatabase) = database.herbDao()
}
