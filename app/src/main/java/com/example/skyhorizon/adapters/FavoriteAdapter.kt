package com.example.skyhorizon.adapters

import android.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyhorizon.models.FavoriteCity

class FavoriteAdapter(
    private var items: MutableList<FavoriteCity>,
    private val onSelect: (FavoriteCity) -> Unit,
    private val onDelete: (FavoriteCity) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavHolder>() {

    class FavHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return FavHolder(view)
    }

    override fun onBindViewHolder(holder: FavHolder, position: Int) {
        val item = items[position]
        holder.text.setTextColor(Color.WHITE)
        holder.text.text = "${item.name}, ${item.region}, ${item.country}"

        holder.itemView.setOnClickListener { onSelect(item) }
        holder.itemView.setOnLongClickListener {
            onDelete(item)
            true
        }
    }

    override fun getItemCount() = items.size
}