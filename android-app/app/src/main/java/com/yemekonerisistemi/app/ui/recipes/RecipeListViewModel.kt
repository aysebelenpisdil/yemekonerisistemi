package com.yemekonerisistemi.app.ui.recipes

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

    // Not: init'te otomatik yükleme yok - Fragment'tan tetikleniyor

    /**
     * Tarifleri yükle
     * @param ingredients Envanterdeki gerçek malzeme listesi
     */
    fun loadRecipes(ingredients: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Boş envanter kontrolü
            if (ingredients.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isEmpty = true,
                    error = "Lütfen önce buzdolabınıza malzeme ekleyin"
                )
                _recipes.value = emptyList()
                return@launch
            }

            try {
                val request = RecipeRecommendationRequest(
                    ingredients = ingredients,
                    dietaryPreferences = null,
                    maxCookingTime = null,
                    maxCalories = null,
                    limit = 20
                )

                android.util.Log.d("RecipeListViewModel", "📤 API'ye gönderilen malzemeler: $ingredients")

                val response = RetrofitClient.apiService.getRecipeRecommendations(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _recipes.value = body.recipes
                    _matchedIngredients.value = body.matchedIngredients
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmpty = body.recipes.isEmpty()
                    )

                    android.util.Log.d("RecipeListViewModel", "✅ ${body.recipes.size} tarif bulundu")
                } else {
                    loadDemoRecipes()
                }
            } catch (e: Exception) {
                android.util.Log.e("RecipeListViewModel", "❌ API hatası: ${e.message}")
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
     * Yenile
     */
    fun refresh() {
        // SharedViewModel'den ingredients alınacak
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
        _recipes.value = DemoDataProvider.getDemoRecipes()
        _uiState.value = _uiState.value.copy(isLoading = false, isEmpty = false)
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
