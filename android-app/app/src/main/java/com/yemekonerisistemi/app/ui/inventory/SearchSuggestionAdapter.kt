package com.yemekonerisistemi.app.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R

/**
 * Arama önerileri için RecyclerView Adapter
 */
class SearchSuggestionAdapter(
    private val onSuggestionClick: (String) -> Unit
) : RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder>() {

    private var suggestions: List<String> = emptyList()

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val suggestionText: TextView = itemView.findViewById(R.id.suggestionText)

        fun bind(suggestion: String) {
            suggestionText.text = suggestion
            itemView.setOnClickListener { onSuggestionClick(suggestion) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size

    fun updateSuggestions(newSuggestions: List<String>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }
}
