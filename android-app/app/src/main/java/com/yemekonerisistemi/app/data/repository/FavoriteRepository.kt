package com.yemekonerisistemi.app.data.repository

import android.content.Context
import com.yemekonerisistemi.app.data.local.AppDatabase
import com.yemekonerisistemi.app.data.local.FavoriteRecipeEntity
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Favorite Repository
 * Favori tariflerin local yönetimi
 */
class FavoriteRepository(context: Context) {

    private val favoriteDao = AppDatabase.getDatabase(context).favoriteDao()

    /**
     * Tüm favorileri getir
     */
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>> {
        return favoriteDao.getAll()
    }

    /**
     * Tarif favori mi? (Flow)
     */
    fun isFavoriteFlow(recipeId: Int): Flow<Boolean> {
        return favoriteDao.isFavoriteFlow(recipeId)
    }

    /**
     * Tarif favori mi? (Suspend)
     */
    suspend fun isFavorite(recipeId: Int): Boolean {
        return favoriteDao.isFavorite(recipeId)
    }

    /**
     * Favorilere ekle
     */
    suspend fun addToFavorites(recipe: Recipe) {
        val entity = FavoriteRecipeEntity(
            recipeId = recipe.id,
            title = recipe.title,
            imageUrl = recipe.imageUrl
        )
        favoriteDao.insert(entity)
    }

    /**
     * Favorilerden çıkar
     */
    suspend fun removeFromFavorites(recipeId: Int) {
        favoriteDao.deleteByRecipeId(recipeId)
    }

    /**
     * Toggle favorite
     */
    suspend fun toggleFavorite(recipe: Recipe): Boolean {
        val isFav = favoriteDao.isFavorite(recipe.id)
        if (isFav) {
            favoriteDao.deleteByRecipeId(recipe.id)
        } else {
            addToFavorites(recipe)
        }
        return !isFav
    }

    /**
     * Favori sayısı
     */
    suspend fun getFavoriteCount(): Int {
        return favoriteDao.getCount()
    }
}
