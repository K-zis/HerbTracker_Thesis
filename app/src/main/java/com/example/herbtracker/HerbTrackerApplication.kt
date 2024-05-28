package com.example.herbtracker

import android.app.Application
import com.example.herbtracker.data.AppContainer
import com.example.herbtracker.data.DefaultAppContainer
import com.example.herbtracker.di.HerbPointProvider
import com.example.herbtracker.di.HerbPointRepository
import com.example.herbtracker.di.NetworkProvider
import com.example.herbtracker.di.NetworkRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HerbTrackerApplication : Application() {
    lateinit var container: AppContainer
    @Inject lateinit var  herbPointRepository: HerbPointRepository
    @Inject lateinit var  networkRepository: NetworkRepository

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(herbPointRepository, networkRepository)


    }


}