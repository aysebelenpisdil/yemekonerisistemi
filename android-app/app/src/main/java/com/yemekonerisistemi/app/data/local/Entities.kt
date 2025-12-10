package com.yemekonerisistemi.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached Recipe - Offline görüntüleme için
 */
@Entity(tableName = "cached_recipes")
data class CachedRecipeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val cookingTime: Int,
    val calories: Int,
    val servings: Int,
    val recommendationReason: String,
    val availableIngredients: String,
    val imageUrl: String,
    val instructions: String, // JSON string olarak saklanır
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Inventory Item - Kullanıcının buzdolabı envanteri
 */
@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Favorite Recipe - Kullanıcının favori tarifleri
 */
@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey
    val recipeId: Int,
    val title: String,
    val imageUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
