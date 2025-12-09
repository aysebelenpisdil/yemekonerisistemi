package com.yemekonerisistemi.app.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.models.InventoryItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Envanter (Buzdolabı) ekranı için ViewModel
 * Malzeme listesi yönetimi, arama, ekleme/silme işlemleri
 */
class InventoryViewModel : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    // Envanter listesi
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    // Arama önerileri
    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    // Arama sorgusu
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Debounce için search job
    private var searchJob: Job? = null

    init {
        loadSampleIngredients()
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
     * Malzeme ekle
     */
    fun addIngredient(ingredientName: String) {
        val currentItems = _inventoryItems.value.toMutableList()

        // Aynı malzeme varsa miktarını artır
        val existingItem = currentItems.find {
            it.name.equals(ingredientName, ignoreCase = true)
        }

        if (existingItem != null) {
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
            _uiState.value = _uiState.value.copy(
                lastAction = ActionResult.QuantityIncreased(ingredientName)
            )
        } else {
            val newItem = InventoryItem(
                name = ingredientName,
                quantity = 1,
                unit = "adet"
            )
            currentItems.add(newItem)
            _uiState.value = _uiState.value.copy(
                lastAction = ActionResult.ItemAdded(ingredientName)
            )
        }

        _inventoryItems.value = currentItems
        clearSearch()
    }

    /**
     * Malzeme sil
     */
    fun removeIngredient(item: InventoryItem) {
        val currentItems = _inventoryItems.value.toMutableList()
        currentItems.remove(item)
        _inventoryItems.value = currentItems
        _uiState.value = _uiState.value.copy(
            lastAction = ActionResult.ItemRemoved(item.name)
        )
    }

    /**
     * Malzeme miktarını güncelle
     */
    fun updateQuantity(item: InventoryItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeIngredient(item)
            return
        }

        val currentItems = _inventoryItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id }

        if (index != -1) {
            currentItems[index] = item.copy(quantity = newQuantity)
            _inventoryItems.value = currentItems
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
     * Demo malzemeler
     */
    private fun loadSampleIngredients() {
        _inventoryItems.value = listOf(
            InventoryItem(name = "Yumurta", quantity = 6, unit = "adet"),
            InventoryItem(name = "Domates", quantity = 3, unit = "adet"),
            InventoryItem(name = "Tavuk Göğsü", quantity = 2, unit = "adet"),
            InventoryItem(name = "Biber", quantity = 4, unit = "adet"),
            InventoryItem(name = "Soğan", quantity = 2, unit = "adet")
        )
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
