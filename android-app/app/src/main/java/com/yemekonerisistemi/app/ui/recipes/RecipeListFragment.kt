package com.yemekonerisistemi.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.api.RecipeRecommendationRequest
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.launch

/**
 * Tarif Listesi Fragment
 * Backend'den tarif önerileri çeker ve gösterir
 */
class RecipeListFragment : Fragment() {

    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var emptyStateLayout: View? = null
    private lateinit var recipeAdapter: RecipeAdapter

    private val recipes = mutableListOf<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'ları bağla
        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        // emptyStateLayout şu anda layout'ta yok, null bırakıyoruz
        // emptyStateLayout = view.findViewById<View?>(R.id.emptyStateLayout)

        // RecyclerView kurulumu
        setupRecyclerView()

        // Backend'den tarifleri yükle
        loadRecipesFromBackend()
    }

    private fun setupRecyclerView() {
        recipesRecyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeAdapter(recipes) { recipe ->
            navigateToRecipeDetail(recipe)
        }
        recipesRecyclerView.adapter = recipeAdapter
    }

    /**
     * Backend'den tarif önerilerini yükle
     */
    private fun loadRecipesFromBackend() {
        // Loading state göster
        showLoading(true)

        lifecycleScope.launch {
            try {
                // Demo: Buzdolabındaki malzemeleri kullan
                val ingredientList = listOf("Tavuk", "Domates", "Biber", "Yumurta", "Soğan")

                val request = RecipeRecommendationRequest(
                    ingredients = ingredientList,
                    dietary_preferences = null,
                    max_cooking_time = null,
                    max_calories = null,
                    limit = 20
                )

                val response = RetrofitClient.apiService.getRecipeRecommendations(request)

                if (response.isSuccessful && response.body() != null) {
                    val recommendationResponse = response.body()!!
                    recipes.clear()
                    recipes.addAll(recommendationResponse.recipes)
                    recipeAdapter.notifyDataSetChanged()

                    // Empty state kontrolü
                    showEmptyState(recipes.isEmpty())
                    showLoading(false)

                    Toast.makeText(
                        context,
                        "${recipes.size} tarif bulundu!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Hata durumunda fallback: Demo tarifleri göster
                    loadDemoRecipes()
                }
            } catch (e: Exception) {
                // Network hatası: Demo tarifleri göster
                showError("Backend'e bağlanılamıyor. Demo verileri gösteriliyor.")
                loadDemoRecipes()
            }
        }
    }

    /**
     * Demo tarifleri yükle (offline fallback)
     */
    private fun loadDemoRecipes() {
        recipes.clear()
        recipes.addAll(getDemoRecipes())
        recipeAdapter.notifyDataSetChanged()
        showEmptyState(false)
        showLoading(false)
    }

    private fun getDemoRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Tavuk Sote",
                cookingTime = 30,
                calories = 280,
                servings = 4,
                recommendationReason = "Tavuk ve sebzelerinle mükemmel uyum!",
                availableIngredients = "Tavuk, Domates, Biber",
                imageUrl = "",
                instructions = listOf(
                    "Tavukları küp doğrayın",
                    "Sebzeleri doğrayın",
                    "Tavada kavurun"
                )
            ),
            Recipe(
                id = 2,
                title = "Kremalı Makarna",
                cookingTime = 20,
                calories = 420,
                servings = 2,
                recommendationReason = "Hızlı yemek tercihine uygun!",
                availableIngredients = "Makarna, Yoğurt",
                imageUrl = "",
                instructions = listOf("Makarnayı haşlayın", "Sos hazırlayın", "Karıştırın")
            ),
            Recipe(
                id = 3,
                title = "Sebze Çorbası",
                cookingTime = 25,
                calories = 150,
                servings = 4,
                recommendationReason = "Sağlıklı ve hafif bir seçim",
                availableIngredients = "Domates, Soğan, Biber",
                imageUrl = "",
                instructions = listOf("Sebzeleri doğrayın", "Kaynatın", "Karıştırın")
            )
        )
    }

    /**
     * Tarif detayına git
     */
    private fun navigateToRecipeDetail(recipe: Recipe) {
        try {
            // Navigation action kullanarak RecipeDetailFragment'a git
            findNavController().navigate(R.id.action_recipeList_to_recipeDetail)
        } catch (e: Exception) {
            Toast.makeText(context, "Tarif detayı açılamadı", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * UI State Yönetimi
     */
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recipesRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(isEmpty: Boolean) {
        emptyStateLayout?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recipesRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }
}