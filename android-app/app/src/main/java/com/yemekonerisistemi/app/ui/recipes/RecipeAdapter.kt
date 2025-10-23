package com.yemekonerisistemi.app.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val calorieText: TextView = itemView.findViewById(R.id.calorieText)
        val recommendationReason: TextView = itemView.findViewById(R.id.recommendationReason)
        val availableIngredients: TextView = itemView.findViewById(R.id.availableIngredients)

        fun bind(recipe: Recipe) {
            recipeTitle.text = recipe.title
            timeText.text = "⏱️ ${recipe.cookingTime} dk"
            calorieText.text = "🔥 ${recipe.calories} kal"
            recommendationReason.text = recipe.recommendationReason
            availableIngredients.text = "Elindekiler: ${recipe.availableIngredients}"

            // Kart tıklama olayı
            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
}