package com.example.skyhorizon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyhorizon.R

class WeatherAdapter(
    private val items: MutableList<String>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {


    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDate: TextView = view.findViewById(R.id.textDate)
        val textMinTemp: TextView = view.findViewById(R.id.textMinTemp)
        val textMaxTemp: TextView = view.findViewById(R.id.textMaxTemp)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = items[position]

        val parts = item.split(":")
        val date = parts.getOrNull(0)?.trim() ?: ""
        val temps = parts.getOrNull(1)?.trim() ?: ""

        val tempParts = temps.split("/")
        val tempMin = tempParts.getOrNull(0)?.trim() ?: ""
        val tempMax = tempParts.getOrNull(1)?.trim() ?: ""

        holder.textDate.text = date
        holder.textMinTemp.text = tempMin
        holder.textMaxTemp.text = tempMax

        holder.itemView.setOnClickListener { onClick(position) }
    }


    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}