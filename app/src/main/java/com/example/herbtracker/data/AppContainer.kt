package com.example.herbtracker.data

import com.example.herbtracker.di.HerbPointProvider
import com.example.herbtracker.di.HerbPointRepository
import com.example.herbtracker.di.NetworkProvider
import com.example.herbtracker.di.NetworkRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject


interface AppContainer {
    val herbPointRepository: HerbPointRepository
    val networkRepository: NetworkRepository
}

@ActivityRetainedScoped
class DefaultAppContainer @Inject constructor(
    override val herbPointRepository: HerbPointRepository,
    override val networkRepository: NetworkRepository
) : AppContainer