package com.example.skyhorizon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyhorizon.network.GeocodingResult
import com.example.skyhorizon.R

class CityAdapter(
    private val items: List<GeocodingResult>,
    private val onClick: (GeocodingResult) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textViewCity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = items[position]
        val regionText = if (!city.admin1.isNullOrEmpty()) ", ${city.admin1}" else ""
        holder.textView.text = "${city.name}$regionText, ${city.country}"
        holder.itemView.setOnClickListener { onClick(city) }
    }

    override fun getItemCount(): Int = items.size
}