package com.yemekonerisistemi.app.ui.home

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
 * Home ekranı için ViewModel
 * Envanter özeti, önerilen ve popüler tarifler
 */
class HomeViewModel : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Envanter sayısı
    private val _inventoryCount = MutableStateFlow(0)
    val inventoryCount: StateFlow<Int> = _inventoryCount.asStateFlow()

    // Önerilen tarifler
    private val _recommendedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recommendedRecipes: StateFlow<List<Recipe>> = _recommendedRecipes.asStateFlow()

    // Popüler tarifler
    private val _trendingRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val trendingRecipes: StateFlow<List<Recipe>> = _trendingRecipes.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        loadInventoryCount()
        loadRecommendedRecipes()
        loadTrendingRecipes()
    }

    private fun loadInventoryCount() {
        // TODO: Backend'den gerçek envanter sayısını çek
        // Şimdilik demo değer
        _inventoryCount.value = 12
    }

    private fun loadRecommendedRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingRecommended = true)
            try {
                val request = RecipeRecommendationRequest(
                    ingredients = listOf("Tavuk", "Domates", "Biber", "Yumurta"),
                    limit = 5
                )
                val response = RetrofitClient.apiService.getRecipeRecommendations(request)

                if (response.isSuccessful && response.body() != null) {
                    _recommendedRecipes.value = response.body()!!.recipes
                } else {
                    _recommendedRecipes.value = getDemoRecommendedRecipes()
                }
            } catch (e: Exception) {
                _recommendedRecipes.value = getDemoRecommendedRecipes()
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingRecommended = false)
            }
        }
    }

    private fun loadTrendingRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrending = true)
            try {
                // TODO: Backend'de trending endpoint eklendiğinde güncellenecek
                _trendingRecipes.value = getDemoTrendingRecipes()
            } catch (e: Exception) {
                _trendingRecipes.value = getDemoTrendingRecipes()
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingTrending = false)
            }
        }
    }

    fun updateInventoryCount(count: Int) {
        _inventoryCount.value = count
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Demo data
    private fun getDemoRecommendedRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Tavuk Sote",
                cookingTime = 30,
                calories = 280,
                recommendationReason = "Envanterindeki tavuk ve sebzelerle mükemmel uyum!",
                availableIngredients = "Tavuk, Domates, Biber",
                imageUrl = ""
            ),
            Recipe(
                id = 2,
                title = "Menemen",
                cookingTime = 15,
                calories = 220,
                recommendationReason = "Yumurta ve domateslerinle hızlı kahvaltı!",
                availableIngredients = "Yumurta, Domates, Biber",
                imageUrl = ""
            ),
            Recipe(
                id = 3,
                title = "Sebze Çorbası",
                cookingTime = 25,
                calories = 150,
                recommendationReason = "Sebzelerini değerlendirmek için ideal",
                availableIngredients = "Domates, Soğan, Biber",
                imageUrl = ""
            )
        )
    }

    private fun getDemoTrendingRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 4,
                title = "Kuru Fasulye",
                cookingTime = 90,
                calories = 350,
                recommendationReason = "Bu hafta en çok yapılan tarif!",
                availableIngredients = "Fasulye, Soğan",
                imageUrl = ""
            ),
            Recipe(
                id = 5,
                title = "Mercimek Çorbası",
                cookingTime = 35,
                calories = 180,
                recommendationReason = "Klasik lezzet, her zaman favoride",
                availableIngredients = "Mercimek, Soğan",
                imageUrl = ""
            ),
            Recipe(
                id = 6,
                title = "Makarna",
                cookingTime = 20,
                calories = 400,
                recommendationReason = "Hızlı ve pratik!",
                availableIngredients = "Makarna",
                imageUrl = ""
            )
        )
    }
}

/**
 * Home ekranı UI durumu
 */
data class HomeUiState(
    val isLoadingRecommended: Boolean = false,
    val isLoadingTrending: Boolean = false,
    val error: String? = null
)
