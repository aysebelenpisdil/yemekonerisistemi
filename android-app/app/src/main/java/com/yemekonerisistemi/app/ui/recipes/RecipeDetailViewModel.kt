package com.yemekonerisistemi.app.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.data.DemoDataProvider
import com.yemekonerisistemi.app.data.repository.FavoriteRepository
import com.yemekonerisistemi.app.models.Ingredient
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Tarif detay ekranı için ViewModel
 * Tarif bilgileri, favori durumu, malzeme kontrolü
 */
class RecipeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteRepository = FavoriteRepository(application)

    // UI State
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    // Tarif detayı
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    // Malzeme listesi
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    // Favori durumu
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Eksik malzeme sayısı
    private val _missingIngredientsCount = MutableStateFlow(0)
    val missingIngredientsCount: StateFlow<Int> = _missingIngredientsCount.asStateFlow()

    /**
     * Tarif detayını yükle
     */
    fun loadRecipeDetail(recipeId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Favori durumunu kontrol et
            _isFavorite.value = favoriteRepository.isFavorite(recipeId)

            try {
                val response = RetrofitClient.apiService.getRecipeById(recipeId)

                if (response.isSuccessful && response.body() != null) {
                    val recipe = response.body()!!
                    _recipe.value = recipe
                    _ingredients.value = recipe.ingredients
                    calculateMissingIngredients(recipe.ingredients)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    loadDemoRecipe()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Tarif yüklenemedi: ${e.message}"
                )
                loadDemoRecipe()
            }
        }
    }

    /**
     * Favori durumunu değiştir - Database'e kaydet
     */
    fun toggleFavorite() {
        val currentRecipe = _recipe.value ?: return

        viewModelScope.launch {
            val newFavoriteState = favoriteRepository.toggleFavorite(currentRecipe)
            _isFavorite.value = newFavoriteState

            _uiState.value = _uiState.value.copy(
                lastAction = if (newFavoriteState) {
                    DetailAction.AddedToFavorites
                } else {
                    DetailAction.RemovedFromFavorites
                }
            )
        }
    }

    /**
     * Eksik malzemeleri alışveriş listesine ekle
     */
    fun addMissingToShoppingList() {
        val missingIngredients = _ingredients.value.filter { !it.isAvailable }
        if (missingIngredients.isNotEmpty()) {
            // TODO: Backend'e alışveriş listesi güncellemesi gönder
            _uiState.value = _uiState.value.copy(
                lastAction = DetailAction.AddedToShoppingList(missingIngredients.size)
            )
        }
    }

    /**
     * Tarifi paylaş
     */
    fun getShareText(): String {
        val recipe = _recipe.value ?: return ""
        return """
            ${recipe.title}
            🕐 ${recipe.cookingTime} dakika | 🔥 ${recipe.calories} kalori
            ${recipe.recommendationReason}
            Yemek Öneri Sistemi ile paylaşıldı.
        """.trimIndent()
    }

    /**
     * Pişirme modunu başlat
     */
    fun startCookingMode() {
        _uiState.value = _uiState.value.copy(
            lastAction = DetailAction.CookingModeStarted
        )
    }

    /**
     * Hatayı temizle
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Son aksiyonu temizle
     */
    fun clearLastAction() {
        _uiState.value = _uiState.value.copy(lastAction = null)
    }

    /**
     * Eksik malzeme sayısını hesapla
     */
    private fun calculateMissingIngredients(ingredients: List<Ingredient>) {
        _missingIngredientsCount.value = ingredients.count { !it.isAvailable }
    }

    /**
     * Demo tarif yükle (offline fallback)
     */
    private fun loadDemoRecipe() {
        _recipe.value = DemoDataProvider.getDemoRecipeDetail()
        _ingredients.value = DemoDataProvider.getDemoIngredients()
        calculateMissingIngredients(_ingredients.value)
        _uiState.value = _uiState.value.copy(isLoading = false)
    }
}

/**
 * Tarif detay UI durumu
 */
data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastAction: DetailAction? = null
)

/**
 * Detay ekranı aksiyonları
 */
sealed class DetailAction {
    object AddedToFavorites : DetailAction()
    object RemovedFromFavorites : DetailAction()
    data class AddedToShoppingList(val count: Int) : DetailAction()
    object CookingModeStarted : DetailAction()
}
