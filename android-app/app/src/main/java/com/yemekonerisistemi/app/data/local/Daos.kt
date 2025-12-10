package com.yemekonerisistemi.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Recipe DAO - Cached recipes için
 */
@Dao
interface RecipeDao {

    @Query("SELECT * FROM cached_recipes ORDER BY cachedAt DESC")
    fun getAllCached(): Flow<List<CachedRecipeEntity>>

    @Query("SELECT * FROM cached_recipes WHERE id = :recipeId")
    suspend fun getById(recipeId: Int): CachedRecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: CachedRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<CachedRecipeEntity>)

    @Delete
    suspend fun delete(recipe: CachedRecipeEntity)

    @Query("DELETE FROM cached_recipes WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("DELETE FROM cached_recipes")
    suspend fun deleteAll()
}

/**
 * Inventory DAO - Buzdolabı envanteri için
 */
@Dao
interface InventoryDao {

    @Query("SELECT * FROM inventory_items ORDER BY addedAt DESC")
    fun getAll(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :itemId")
    suspend fun getById(itemId: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<InventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItemEntity>)

    @Update
    suspend fun update(item: InventoryItemEntity)

    @Delete
    suspend fun delete(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE id = :itemId")
    suspend fun deleteById(itemId: String)

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM inventory_items")
    suspend fun getCount(): Int
}

/**
 * Favorite DAO - Favori tarifler için
 */
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_recipes ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE recipeId = :recipeId)")
    suspend fun isFavorite(recipeId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE recipeId = :recipeId)")
    fun isFavoriteFlow(recipeId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteRecipeEntity)

    @Query("DELETE FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun deleteByRecipeId(recipeId: Int)

    @Query("DELETE FROM favorite_recipes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM favorite_recipes")
    suspend fun getCount(): Int
}
