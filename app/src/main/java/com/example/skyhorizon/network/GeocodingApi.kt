package com.example.skyhorizon.network

import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val country: String,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double
)


interface GeocodingApi {
    @GET("search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): GeocodingResponse
}
