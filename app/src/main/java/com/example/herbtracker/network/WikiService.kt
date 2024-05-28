package com.example.herbtracker.network

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiService {

    @GET("api.php")
    suspend fun getWikiDescription (
        @Query("format") format: String = "json",
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "extracts",
        @Query("exintro") exintro: String? = "",
        @Query("explaintext") explaintex: String? = "",
        @Query("redirects") redirects: String = "1",
        @Query("titles") herbname: String
    ) : JsonObject

}