package com.yemekonerisistemi.app.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RecipeRecommendationRequest
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Tarif listesi ekranı için ViewModel
 * Backend'den tarif önerileri ve arama
 */
class RecipeListViewModel : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    // Tarif listesi
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // Eşleşen malzemeler
    private val _matchedIngredients = MutableStateFlow<List<String>>(emptyList())
    val matchedIngredients: StateFlow<List<String>> = _matchedIngredients.asStateFlow()

    init {
        loadRecipes()
    }

    /**
     * Tarifleri yükle
     */
    fun loadRecipes(ingredients: List<String>? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val ingredientList = ingredients ?: listOf("Tavuk", "Domates", "Biber", "Yumurta", "Soğan")

                val request = RecipeRecommendationRequest(
                    ingredients = ingredientList,
                    dietary_preferences = null,
                    max_cooking_time = null,
                    max_calories = null,
                    limit = 20
                )

                val response = RetrofitClient.apiService.getRecipeRecommendations(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _recipes.value = body.recipes
                    _matchedIngredients.value = body.matched_ingredients
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmpty = body.recipes.isEmpty()
                    )
                } else {
                    loadDemoRecipes()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Backend'e bağlanılamıyor: ${e.message}"
                )
                loadDemoRecipes()
            }
        }
    }

    /**
     * Tarif ara
     */
    fun searchRecipes(query: String) {
        if (query.isEmpty()) {
            loadRecipes()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = RetrofitClient.apiService.searchRecipes(query, 20)

                if (response.isSuccessful && response.body() != null) {
                    _recipes.value = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmpty = response.body()!!.isEmpty()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Arama sonucu bulunamadı"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Arama hatası: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtreli tarif yükle
     */
    fun loadFilteredRecipes(
        ingredients: List<String>,
        dietaryPreferences: List<String>? = null,
        maxCookingTime: Int? = null,
        maxCalories: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val request = RecipeRecommendationRequest(
                    ingredients = ingredients,
                    dietary_preferences = dietaryPreferences,
                    max_cooking_time = maxCookingTime,
                    max_calories = maxCalories,
                    limit = 20
                )

                val response = RetrofitClient.apiService.getRecipeRecommendations(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _recipes.value = body.recipes
                    _matchedIngredients.value = body.matched_ingredients
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmpty = body.recipes.isEmpty()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Tarif bulunamadı"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Bağlantı hatası: ${e.message}"
                )
            }
        }
    }

    /**
     * Yenile
     */
    fun refresh() {
        loadRecipes()
    }

    /**
     * Hatayı temizle
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Demo tarifleri yükle (offline fallback)
     */
    private fun loadDemoRecipes() {
        _recipes.value = listOf(
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
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isEmpty = false
        )
    }
}

/**
 * Tarif listesi UI durumu
 */
data class RecipeListUiState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val error: String? = null
)
