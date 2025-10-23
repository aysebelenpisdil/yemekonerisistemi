package com.yemekonerisistemi.app.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R

/**
 * Arama önerileri için adapter (Trendyol-style)
 */
class SearchSuggestionAdapter(
    private val suggestions: MutableList<String> = mutableListOf(),
    private val onSuggestionClick: (String) -> Unit
) : RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val suggestionText: TextView = view.findViewById(R.id.suggestionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.suggestionText.text = suggestion

        // Click listener
        holder.itemView.setOnClickListener {
            onSuggestionClick(suggestion)
        }
    }

    override fun getItemCount(): Int = suggestions.size

    /**
     * Önerileri güncelle
     */
    fun updateSuggestions(newSuggestions: List<String>) {
        suggestions.clear()
        suggestions.addAll(newSuggestions)
        notifyDataSetChanged()
    }

    /**
     * Önerileri temizle
     */
    fun clearSuggestions() {
        suggestions.clear()
        notifyDataSetChanged()
    }
}
