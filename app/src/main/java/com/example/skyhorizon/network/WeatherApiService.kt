package com.example.skyhorizon.network

import com.example.skyhorizon.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode",
        @Query("forecast_days") days: Int = 14,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}