package com.yemekonerisistemi.app.ui.recipes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Ingredient

/**
 * Tarif Detayındaki Malzemeler Adapter
 * Spec Kit'e göre: Mevcut/Eksik malzeme gösterimi
 */
class IngredientDetailAdapter(
    private val ingredients: List<Ingredient>
) : RecyclerView.Adapter<IngredientDetailAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val availableCheckbox: CheckBox = itemView.findViewById(R.id.availableCheckbox)
        private val ingredientNameText: TextView = itemView.findViewById(R.id.ingredientNameText)
        private val ingredientAmountText: TextView = itemView.findViewById(R.id.ingredientAmountText)
        private val statusBadge: TextView = itemView.findViewById(R.id.statusBadge)

        fun bind(ingredient: Ingredient) {
            ingredientNameText.text = ingredient.name
            ingredientAmountText.text = "${ingredient.amount} ${ingredient.unit}"

            availableCheckbox.isChecked = ingredient.isAvailable

            if (ingredient.isAvailable) {
                statusBadge.text = "✓ Var"
                statusBadge.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                ingredientNameText.setTextColor(Color.BLACK)
            } else {
                statusBadge.text = "✗ Eksik"
                statusBadge.setBackgroundColor(Color.parseColor("#F44336")) // Red
                ingredientNameText.setTextColor(Color.GRAY)
            }
        }
    }
}
