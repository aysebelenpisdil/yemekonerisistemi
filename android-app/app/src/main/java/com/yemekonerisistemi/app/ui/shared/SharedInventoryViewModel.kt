package com.yemekonerisistemi.app.ui.shared

import androidx.lifecycle.ViewModel
import com.yemekonerisistemi.app.models.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Activity-scoped SharedViewModel
 * InventoryFragment ve RecipeListFragment arasında envanter paylaşımı
 */
class SharedInventoryViewModel : ViewModel() {

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    /**
     * Envanter listesini güncelle
     */
    fun updateInventory(items: List<InventoryItem>) {
        _inventoryItems.value = items
    }

    /**
     * Malzeme isimlerini al (tarif önerisi için)
     */
    fun getIngredientNames(): List<String> = _inventoryItems.value.map { it.name }

    /**
     * Envanter boş mu?
     */
    fun isEmpty(): Boolean = _inventoryItems.value.isEmpty()

    /**
     * Envanterdeki öğe sayısını al
     */
    fun getItemCount(): Int = _inventoryItems.value.size

    /**
     * Toplam miktarı al
     */
    fun getTotalQuantity(): Int = _inventoryItems.value.sumOf { it.quantity }
}
