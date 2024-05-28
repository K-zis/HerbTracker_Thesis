package com.example.herbtracker.di

import com.example.herbtracker.network.WikiService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@ActivityRetainedScoped
class NetworkRepository @Inject constructor(private val client: WikiService) {

    suspend fun getWikiDescriptionRequest(herbname: String): JsonObject {
      return  client.getWikiDescription(herbname = herbname)
    }
}