package com.example.skyhorizon.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyhorizon.R
import com.example.skyhorizon.adapters.CityAdapter
import com.example.skyhorizon.adapters.WeatherAdapter
import com.example.skyhorizon.managers.AuthManager
import com.example.skyhorizon.managers.FirestoreManager
import com.example.skyhorizon.models.FavoriteCity
import com.example.skyhorizon.models.WeatherResponse
import com.example.skyhorizon.network.GeocodingResult
import com.example.skyhorizon.network.RetrofitGeocoding
import com.example.skyhorizon.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewWeather: RecyclerView
    private lateinit var recyclerViewSuggestions: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var cityAdapter: CityAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var editCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var textCity: TextView
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient


    private var addFavoriteMenuItem: MenuItem? = null

    private val weatherData = mutableListOf<String>()
    private var responseGlobal: WeatherResponse? = null
    private var selectedLatitude = 47.63
    private var selectedLongitude = 26.25
    private var selectedCityText = "Suceava, Suceava, Romania"

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var citySuggestions = listOf<GeocodingResult>()

    private val currentUid = AuthManager.currentUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // asigură-te că ai acest string
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerViewWeather = findViewById(R.id.recyclerViewWeather)
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions)
        progressBar = findViewById(R.id.progressBar)
        editCity = findViewById(R.id.editCity)
        btnSearch = findViewById(R.id.btnSearch)
        textCity = findViewById(R.id.textCity)

        recyclerViewWeather.layoutManager = LinearLayoutManager(this)
        weatherAdapter = WeatherAdapter(
            items = weatherData,
            onClick = { index -> openDetails(index) },
        )
        recyclerViewWeather.adapter = weatherAdapter

        recyclerViewSuggestions.layoutManager = LinearLayoutManager(this)

        textCity.text = selectedCityText
        fetchWeather()

        btnSearch.setOnClickListener { searchCity() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        addFavoriteMenuItem = menu?.findItem(R.id.action_add_favorite)

        updateFavoriteIcon()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_refresh -> fetchWeather()
            R.id.action_logout -> logout()
            R.id.action_add_favorite -> toggleFavorite()
            R.id.action_favorites -> {
                val intent = Intent(this, FavoriteCitiesActivity::class.java)
                startActivityForResult(intent, 99)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleFavorite() {
        val uid = currentUid ?: return
        val city = FavoriteCity(
            id = "${selectedCityText}_${selectedLatitude}_${selectedLongitude}",
            name = selectedCityText.split(",")[0].trim(),
            region = selectedCityText.split(",").getOrNull(1)?.trim() ?: "",
            country = selectedCityText.split(",").getOrNull(2)?.trim() ?: "",
            latitude = selectedLatitude,
            longitude = selectedLongitude
        )

        FirestoreManager.getFavoriteCities(uid) { favorites ->
            val isFav = favorites.any { it.latitude == city.latitude && it.longitude == city.longitude }
            if (isFav) {
                FirestoreManager.removeFavoriteCity(uid, city.id) { success ->
                    if (success) updateFavoriteIcon(false)
                }
            } else {
                FirestoreManager.addFavoriteCity(city, uid) { success ->
                    if (success) updateFavoriteIcon(true)
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean? = null) {
        val uid = currentUid ?: return
        if (isFavorite != null) {
            addFavoriteMenuItem?.icon = if (isFavorite) {
                getDrawable(R.drawable.ic_favourite)
            } else {
                getDrawable(R.drawable.ic_unfavourite)
            }
        } else {
            FirestoreManager.getFavoriteCities(uid) { favorites ->
                val isFav = favorites.any { it.latitude == selectedLatitude && it.longitude == selectedLongitude }
                addFavoriteMenuItem?.icon = if (isFav) {
                    getDrawable(R.drawable.ic_favourite)
                } else {
                    getDrawable(R.drawable.ic_unfavourite)
                }
            }
        }
    }

    private fun fetchWeather() {
        progressBar.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getWeather(selectedLatitude, selectedLongitude)
                }
                responseGlobal = response

                val newData = mutableListOf<String>()
                for (i in response.daily.time.indices) {
                    val item = "${response.daily.time[i]}: " +
                            "${response.daily.temperature_2m_min[i]}°C / " +
                            "${response.daily.temperature_2m_max[i]}°C"
                    newData.add(item)
                }
                weatherAdapter.updateData(newData)
                progressBar.visibility = View.GONE

                updateFavoriteIcon()
            } catch (e: Exception) {
                e.printStackTrace()
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Eroare la preluarea vremii", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchCity() {
        val name = editCity.text.toString()
        if (name.isBlank()) return

        progressBar.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { RetrofitGeocoding.api.searchCity(name, 5) }
                citySuggestions = response.results ?: emptyList()

                if (citySuggestions.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Niciun oraș găsit", Toast.LENGTH_SHORT).show()
                    recyclerViewSuggestions.visibility = View.GONE
                } else {
                    cityAdapter = CityAdapter(citySuggestions) { city ->
                        selectedLatitude = city.latitude
                        selectedLongitude = city.longitude
                        val region = city.admin1?.let { "$it, " } ?: ""
                        selectedCityText = "${city.name}, $region${city.country}"
                        textCity.text = selectedCityText
                        recyclerViewSuggestions.visibility = View.GONE
                        fetchWeather()
                    }
                    recyclerViewSuggestions.adapter = cityAdapter
                    recyclerViewSuggestions.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Eroare la căutarea orașului", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun openDetails(index: Int) {
        val resp = responseGlobal ?: return
        val intent = Intent(this, WeatherDetailsActivity::class.java)
        intent.putExtra("date", resp.daily.time[index])
        intent.putExtra("tempMin", resp.daily.temperature_2m_min[index].toString())
        intent.putExtra("tempMax", resp.daily.temperature_2m_max[index].toString())
        intent.putExtra("weatherCode", resp.daily.weathercode[index])
        intent.putExtra("cityText", selectedCityText)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 99 && resultCode == RESULT_OK && data != null) {
            selectedLatitude = data.getDoubleExtra("lat", selectedLatitude)
            selectedLongitude = data.getDoubleExtra("lon", selectedLongitude)
            selectedCityText = data.getStringExtra("cityText") ?: selectedCityText
            textCity.text = selectedCityText
            fetchWeather()
        }
    }

    private fun logout() {
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}

