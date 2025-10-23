package com.yemekonerisistemi.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe

class RecipeListFragment : Fragment() {

    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipeCountText: TextView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView)

        // RecyclerView kurulumu
        recipesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Örnek tarif verileri
        val sampleRecipes = getSampleRecipes()

        // Adapter'ı oluştur ve RecyclerView'a ata
        recipeAdapter = RecipeAdapter(sampleRecipes) { recipe ->
            // Tarif detayına git
            openRecipeDetail(recipe)
        }
        recipesRecyclerView.adapter = recipeAdapter
    }

    private fun getSampleRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Tavuk Sote",
                cookingTime = 30,
                calories = 280,
                recommendationReason = "Tavuk ve sebzelerinle mükemmel uyum!",
                availableIngredients = "Tavuk, Domates, Biber",
                imageUrl = ""
            ),
            Recipe(
                id = 2,
                title = "Kremalı Makarna",
                cookingTime = 20,
                calories = 420,
                recommendationReason = "Hızlı yemek tercihine uygun!",
                availableIngredients = "Makarna, Yoğurt",
                imageUrl = ""
            ),
            Recipe(
                id = 3,
                title = "Sebze Çorbası",
                cookingTime = 25,
                calories = 150,
                recommendationReason = "Sağlıklı ve hafif bir seçim",
                availableIngredients = "Domates, Soğan, Biber",
                imageUrl = ""
            )
        )
    }

    private fun openRecipeDetail(recipe: Recipe) {
        // TODO: Navigate to recipe detail
    }
}