package com.example.skyhorizon.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skyhorizon.R
import android.widget.ImageView
import com.bumptech.glide.Glide

class WeatherDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        val city = intent.getStringExtra("cityText") ?: "N/A"
        val date = intent.getStringExtra("date") ?: "N/A"
        val tempMin = intent.getStringExtra("tempMin") ?: "N/A"
        val tempMax = intent.getStringExtra("tempMax") ?: "N/A"
        val weatherCode = intent.getIntExtra("weatherCode", -1)

        findViewById<TextView>(R.id.textCity).text = "$city"
        findViewById<TextView>(R.id.textDate).text = "$date"
        findViewById<TextView>(R.id.textTempMin).text = "Temperatura minimă: $tempMin°C"
        findViewById<TextView>(R.id.textTempMax).text = "Temperatura maximă: $tempMax°C"
        val description = getWeatherDescription(weatherCode)
        findViewById<TextView>(R.id.textWeatherCode).text =
            "Stare meteo: $description (Cod: $weatherCode)"

        val gifView = findViewById<ImageView>(R.id.weatherGif)
        val gifRes = getWeatherGif(weatherCode)

        Glide.with(this)
            .asGif()
            .load(gifRes)
            .into(gifView)

    }

    private fun getWeatherDescription(code: Int): String {
        return when(code) {
            0 -> "Senin"
            1 -> "Parțial senin"
            2 -> "Parțial noros"
            3 -> "Noros"
            45 -> "Ceață"
            48 -> "Ceață cu depunere de chiciură"
            51, 53, 55 -> "Burniță"
            56, 57 -> "Burniță înghețată"
            61, 63, 65 -> "Ploaie"
            66, 67 -> "Ploaie înghețată"
            71, 73, 75 -> "Ninsoare"
            77 -> "Fulgi mici de zăpadă"
            80, 81, 82 -> "Averse de ploaie"
            85, 86 -> "Averse de ninsoare"
            95 -> "Furtună"
            96, 99 -> "Furtună cu grindină"
            else -> "Necunoscut"
        }
    }

    private fun getWeatherGif(code: Int): Int {
        return when(code) {
            0 -> R.drawable.sunny
            1, 2 -> R.drawable.sunny_cloudy
            3 -> R.drawable.cloudy
            45, 48 -> R.drawable.fog
            51, 53, 55 -> R.drawable.drizzle
            56, 57 -> R.drawable.freezing_drizzle
            61, 63, 65, 80, 81, 82 -> R.drawable.rain
            66, 67 -> R.drawable.freezing_rain
            71, 73, 75, 85, 86 -> R.drawable.snow
            95, 96, 99 -> R.drawable.storm
            else -> R.drawable.cloudy
        }
    }
}
