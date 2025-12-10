package com.yemekonerisistemi.app.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe

/**
 * RecyclerView Adapter for Recipe list
 * DiffUtil ile optimize edilmiş
 */
class RecipeAdapter(
    private var recipes: List<Recipe> = emptyList(),
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ImageView? = itemView.findViewById(R.id.recipeImage)
        private val recipeTitle: TextView? = itemView.findViewById(R.id.recipeTitle)
        private val cookingTime: TextView? = itemView.findViewById(R.id.cookingTime)
        private val calories: TextView? = itemView.findViewById(R.id.calories)
        private val matchedIngredients: TextView? = itemView.findViewById(R.id.matchedIngredients)

        fun bind(recipe: Recipe) {
            recipeTitle?.text = recipe.title
            cookingTime?.text = "${recipe.cookingTime} dk"
            calories?.text = "${recipe.calories} kcal"
            matchedIngredients?.text = recipe.availableIngredients

            // Glide ile güvenli görsel yükleme
            recipeImage?.let { imageView ->
                loadRecipeImage(imageView, recipe.imageUrl)
            }

            itemView.setOnClickListener { onRecipeClick(recipe) }
        }

        private fun loadRecipeImage(imageView: ImageView, imageUrl: String?) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .fallback(R.drawable.placeholder_food)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()

            val finalUrl = if (imageUrl.isNullOrBlank()) null else imageUrl

            Glide.with(imageView.context)
                .load(finalUrl)
                .apply(requestOptions)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}