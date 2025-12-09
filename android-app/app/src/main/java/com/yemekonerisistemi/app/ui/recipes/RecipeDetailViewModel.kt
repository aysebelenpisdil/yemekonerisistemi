package com.yemekonerisistemi.app.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RetrofitClient
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
class RecipeDetailViewModel : ViewModel() {

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
     * Favori durumunu değiştir
     */
    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
        _uiState.value = _uiState.value.copy(
            lastAction = if (_isFavorite.value) {
                DetailAction.AddedToFavorites
            } else {
                DetailAction.RemovedFromFavorites
            }
        )
        // TODO: Backend'e favori durumunu kaydet
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

            🕐 ${recipe.cookingTime} dakika
            🔥 ${recipe.calories} kalori

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
        val demoRecipe = Recipe(
            id = 1,
            title = "Tavuk Sote",
            cookingTime = 30,
            calories = 280,
            servings = 4,
            recommendationReason = "Bu tarif envanterinizdeki tavuk, domates ve biber ile mükemmel uyum sağlıyor. " +
                    "Ayrıca günlük kalori hedefinize uygun ve protein değeri yüksek. " +
                    "Hazırlanması kolay ve 30 dakikada hazır!",
            availableIngredients = "Tavuk, Domates, Biber",
            imageUrl = "",
            instructions = listOf(
                "Tavuk göğüslerini küp şeklinde doğrayın ve tuzlayın.",
                "Domatesleri ve biberleri küp şeklinde doğrayın.",
                "Soğanı ince ince doğrayın.",
                "Tavada sıvı yağı kızdırın ve tavukları ekleyin.",
                "Tavuklar renk alana kadar kavurun (yaklaşık 5-7 dakika).",
                "Soğanları ekleyip pembeleşene kadar kavurun.",
                "Domatesleri ve biberleri ekleyin.",
                "Kapağını kapatıp kısık ateşte sebzeler yumuşayana kadar pişirin (15-20 dakika).",
                "Tuz ve karabiberle tatlandırın.",
                "Sıcak servis yapın. Afiyet olsun!"
            )
        )

        val demoIngredients = listOf(
            Ingredient(1, "Tavuk Göğsü", "500", "gram", "Et", isAvailable = true),
            Ingredient(2, "Domates", "3", "adet", "Sebze", isAvailable = true),
            Ingredient(3, "Yeşil Biber", "2", "adet", "Sebze", isAvailable = true),
            Ingredient(4, "Soğan", "1", "adet", "Sebze", isAvailable = true),
            Ingredient(5, "Sıvı Yağ", "2", "yemek kaşığı", "Yağ", isAvailable = false),
            Ingredient(6, "Tuz", "1", "çay kaşığı", "Baharat", isAvailable = true),
            Ingredient(7, "Karabiber", "1", "çay kaşığı", "Baharat", isAvailable = false)
        )

        _recipe.value = demoRecipe
        _ingredients.value = demoIngredients
        calculateMissingIngredients(demoIngredients)
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
