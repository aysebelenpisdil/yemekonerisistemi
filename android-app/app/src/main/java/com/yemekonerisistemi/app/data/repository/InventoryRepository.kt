package com.yemekonerisistemi.app.data.repository

import android.content.Context
import com.yemekonerisistemi.app.data.local.AppDatabase
import com.yemekonerisistemi.app.data.local.InventoryItemEntity
import com.yemekonerisistemi.app.models.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Inventory Repository
 * Local database ile envanter yönetimi
 */
class InventoryRepository(context: Context) {

    private val inventoryDao = AppDatabase.getDatabase(context).inventoryDao()

    /**
     * Tüm envanter öğelerini getir (Flow)
     */
    fun getAllItems(): Flow<List<InventoryItem>> {
        return inventoryDao.getAll().map { entities ->
            entities.map { it.toModel() }
        }
    }

    /**
     * Malzeme ekle
     */
    suspend fun addItem(item: InventoryItem) {
        inventoryDao.insert(item.toEntity())
    }

    /**
     * Malzeme güncelle
     */
    suspend fun updateItem(item: InventoryItem) {
        inventoryDao.update(item.toEntity())
    }

    /**
     * Malzeme sil
     */
    suspend fun deleteItem(item: InventoryItem) {
        inventoryDao.deleteById(item.id)
    }

    /**
     * Tüm envanteri temizle
     */
    suspend fun clearAll() {
        inventoryDao.deleteAll()
    }

    /**
     * Envanter sayısını getir
     */
    suspend fun getItemCount(): Int {
        return inventoryDao.getCount()
    }

    /**
     * Malzeme isimlerini getir (tarif önerisi için)
     */
    fun getIngredientNames(): Flow<List<String>> {
        return inventoryDao.getAll().map { entities ->
            entities.map { it.name }
        }
    }

    // Extension functions for mapping
    private fun InventoryItemEntity.toModel(): InventoryItem {
        return InventoryItem(
            id = this.id,
            name = this.name,
            quantity = this.quantity,
            unit = this.unit,
            category = this.category
        )
    }

    private fun InventoryItem.toEntity(): InventoryItemEntity {
        return InventoryItemEntity(
            id = this.id,
            name = this.name,
            quantity = this.quantity,
            unit = this.unit,
            category = this.category
        )
    }
}
