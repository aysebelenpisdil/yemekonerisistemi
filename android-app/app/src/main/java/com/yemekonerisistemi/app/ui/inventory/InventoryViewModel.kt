package com.yemekonerisistemi.app.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.api.SemanticSearchRequest
import com.yemekonerisistemi.app.data.DemoDataProvider
import com.yemekonerisistemi.app.data.repository.InventoryRepository
import com.yemekonerisistemi.app.models.InventoryItem
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Envanter ViewModel - Room Database ile persist
 */
class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    private val _semanticResults = MutableStateFlow<List<Recipe>>(emptyList())
    val semanticResults: StateFlow<List<Recipe>> = _semanticResults.asStateFlow()

    private val _isSemanticMode = MutableStateFlow(false)
    val isSemanticMode: StateFlow<Boolean> = _isSemanticMode.asStateFlow()

    init {
        loadInventoryFromDatabase()
    }

    /**
     * Database'den envanter yükle
     */
    private fun loadInventoryFromDatabase() {
        viewModelScope.launch {
            repository.getAllItems().collect { items ->
                _inventoryItems.value = items

                // İlk kez açılıyorsa demo data ekle
                if (items.isEmpty()) {
                    loadSampleIngredients()
                }
            }
        }
    }

    /**
     * Demo malzemeler (ilk açılış için)
     */
    private fun loadSampleIngredients() {
        viewModelScope.launch {
            DemoDataProvider.getDemoInventoryItems().forEach { item ->
                repository.addItem(item)
            }
        }
    }

    /**
     * Real-time fuzzy search (debounced)
     */
    fun searchIngredients(query: String) {
        _searchQuery.value = query

        // Önceki aramayı iptal et
        searchJob?.cancel()

        if (query.isEmpty()) {
            _searchSuggestions.value = emptyList()
            _uiState.value = _uiState.value.copy(showSuggestions = false)
            return
        }

        if (query.length < 2) {
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce

            _uiState.value = _uiState.value.copy(isSearching = true)

            try {
                val response = RetrofitClient.apiService.searchIngredients(query, 20)

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.results.map { it.name }
                    _searchSuggestions.value = results
                    _uiState.value = _uiState.value.copy(
                        showSuggestions = results.isNotEmpty(),
                        isSearching = false
                    )
                } else {
                    _searchSuggestions.value = emptyList()
                    _uiState.value = _uiState.value.copy(
                        showSuggestions = false,
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Arama hatası: ${e.message}",
                    isSearching = false,
                    showSuggestions = false
                )
            }
        }
    }

    /**
     * Malzeme ekle - Database'e kaydet
     */
    fun addIngredient(ingredientName: String) {
        viewModelScope.launch {
            val existingItem = _inventoryItems.value.find {
                it.name.equals(ingredientName, ignoreCase = true)
            }

            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                repository.updateItem(updatedItem)
                _uiState.value = _uiState.value.copy(
                    lastAction = ActionResult.QuantityIncreased(ingredientName)
                )
            } else {
                val newItem = InventoryItem(
                    name = ingredientName,
                    quantity = 1,
                    unit = "adet"
                )
                repository.addItem(newItem)
                _uiState.value = _uiState.value.copy(
                    lastAction = ActionResult.ItemAdded(ingredientName)
                )
            }
            clearSearch()
        }
    }

    /**
     * Malzeme sil - Database'den kaldır
     */
    fun removeIngredient(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _uiState.value = _uiState.value.copy(
                lastAction = ActionResult.ItemRemoved(item.name)
            )
        }
    }

    /**
     * Malzeme miktarını güncelle
     */
    fun updateQuantity(item: InventoryItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity <= 0) {
                removeIngredient(item)
            } else {
                val updatedItem = item.copy(quantity = newQuantity)
                repository.updateItem(updatedItem)
            }
        }
    }

    /**
     * Aramayı temizle
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchSuggestions.value = emptyList()
        _uiState.value = _uiState.value.copy(showSuggestions = false)
    }

    /**
     * Hata mesajını temizle
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
     * Toplam malzeme sayısı
     */
    fun getTotalItemCount(): Int = _inventoryItems.value.size

    /**
     * Toplam adet sayısı
     */
    fun getTotalQuantity(): Int = _inventoryItems.value.sumOf { it.quantity }

    /**
     * Malzeme isimlerini al (tarif arama için)
     */
    fun getIngredientNames(): List<String> = _inventoryItems.value.map { it.name }

    /**
     * Doğal dil ile semantik arama
     * Örnek: "hafif bir akşam yemeği", "protein ağırlıklı tarif"
     */
    fun semanticSearch(query: String) {
        if (query.length < 3) {
            _semanticResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            _isSemanticMode.value = true

            try {
                val request = SemanticSearchRequest(
                    query = query,
                    searchType = "recipes",
                    limit = 10
                )

                val response = RetrofitClient.apiService.semanticSearch(request)

                if (response.isSuccessful && response.body() != null) {
                    _semanticResults.value = response.body()!!.results
                } else {
                    _semanticResults.value = emptyList()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Semantik arama şu an kullanılamıyor")
            } finally {
                _uiState.value = _uiState.value.copy(isSearching = false)
            }
        }
    }

    /**
     * Semantik moddan çık
     */
    fun exitSemanticMode() {
        _isSemanticMode.value = false
        _semanticResults.value = emptyList()
    }
}

/**
 * Envanter UI durumu
 */
data class InventoryUiState(
    val isSearching: Boolean = false,
    val showSuggestions: Boolean = false,
    val error: String? = null,
    val lastAction: ActionResult? = null
)

/**
 * Kullanıcı aksiyonları sonucu
 */
sealed class ActionResult {
    data class ItemAdded(val name: String) : ActionResult()
    data class ItemRemoved(val name: String) : ActionResult()
    data class QuantityIncreased(val name: String) : ActionResult()
}
