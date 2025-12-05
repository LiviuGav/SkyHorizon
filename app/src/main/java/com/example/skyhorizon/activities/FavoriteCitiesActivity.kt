package com.example.skyhorizon.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyhorizon.managers.AuthManager
import com.example.skyhorizon.adapters.FavoriteAdapter
import com.example.skyhorizon.models.FavoriteCity
import com.example.skyhorizon.managers.FirestoreManager
import com.example.skyhorizon.R

class FavoriteCitiesActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private val favList = mutableListOf<FavoriteCity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recycler = findViewById(R.id.recyclerFavorites)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = FavoriteAdapter(
            items = favList,
            onSelect = { city ->
                val intent = Intent().apply {
                    putExtra("lat", city.latitude)
                    putExtra("lon", city.longitude)
                    putExtra("cityText", "${city.name}, ${city.region}, ${city.country}")
                }
                setResult(RESULT_OK, intent)
                finish()
            },
            onDelete = { city ->
                val uid = AuthManager.currentUid()
                if (uid == null) {
                    Toast.makeText(this, "Utilizatorul nu este logat!", Toast.LENGTH_SHORT).show()
                    return@FavoriteAdapter
                }
                FirestoreManager.removeFavoriteCity(uid, city.id) { success ->
                    if (success) {
                        favList.remove(city)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "Eroare la ștergere", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        recycler.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        val uid = AuthManager.currentUid()
        if (uid == null) {
            Toast.makeText(this, "Utilizatorul nu este logat!", Toast.LENGTH_SHORT).show()
            return
        }

        FirestoreManager.getFavoriteCities(uid) { list ->
            if (list.isEmpty()) {
                Toast.makeText(this, "Nu există orașe favorite", Toast.LENGTH_SHORT).show()
            }

            favList.clear()
            favList.addAll(list)
            adapter.notifyDataSetChanged()

        }
    }
}