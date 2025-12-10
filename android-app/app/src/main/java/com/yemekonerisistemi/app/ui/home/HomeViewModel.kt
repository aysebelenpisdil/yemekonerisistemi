package com.yemekonerisistemi.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RecipeRecommendationRequest
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.data.DemoDataProvider
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
        // Demo değer - SharedViewModel entegrasyonu HomeFragment'ta yapılacak
        _inventoryCount.value = DemoDataProvider.getDemoInventoryCount()
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
                    _recommendedRecipes.value = DemoDataProvider.getRecommendedRecipes()
                }
            } catch (e: Exception) {
                _recommendedRecipes.value = DemoDataProvider.getRecommendedRecipes()
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
                _trendingRecipes.value = DemoDataProvider.getTrendingRecipes()
            } catch (e: Exception) {
                _trendingRecipes.value = DemoDataProvider.getTrendingRecipes()
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingTrending = false)
            }
        }
    }

    /**
     * SharedViewModel'den envanter sayısını güncelle
     * HomeFragment'tan çağrılacak
     */
    fun updateInventoryFromShared(count: Int) {
        _inventoryCount.value = count
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
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
