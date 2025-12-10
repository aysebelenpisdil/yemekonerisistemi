package com.yemekonerisistemi.app.data.local

import android.content.Context
import com.yemekonerisistemi.app.data.model.UserContext
import com.yemekonerisistemi.app.data.prefs.UserPreferences
import com.yemekonerisistemi.app.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Provider for combining user preferences with inventory data
 * to create a complete UserContext for API calls
 */
class UserContextProvider(context: Context) {

    private val userPreferences = UserPreferences(context)
    private val inventoryRepository = InventoryRepository(context)

    /**
     * Get current user context as a Flow
     * Combines preferences and inventory data
     */
    fun getUserContextFlow(): Flow<UserContext> {
        // Combine first 3 flows, then combine with remaining flows
        val preferencesFlow = combine(
            userPreferences.dietTypes,
            userPreferences.allergens,
            userPreferences.cuisines
        ) { dietTypes, allergens, cuisines ->
            Triple(dietTypes, allergens, cuisines)
        }

        val constraintsFlow = combine(
            userPreferences.maxCookingTime,
            userPreferences.maxCalories,
            inventoryRepository.getIngredientNames()
        ) { maxCookingTime, maxCalories, ingredients ->
            Triple(maxCookingTime, maxCalories, ingredients)
        }

        return combine(preferencesFlow, constraintsFlow) { prefs, constraints ->
            UserContext(
                dietTypes = prefs.first.toList(),
                allergens = prefs.second.toList(),
                cuisines = prefs.third.toList(),
                availableIngredients = constraints.third,
                maxCookingTime = constraints.first,
                maxCalories = constraints.second
            )
        }
    }

    /**
     * Get current user context (one-shot, suspend)
     */
    suspend fun getUserContext(): UserContext {
        return getUserContextFlow().first()
    }

    /**
     * Get user context with custom ingredients (overrides inventory)
     */
    suspend fun getUserContextWithIngredients(ingredients: List<String>): UserContext {
        val dietTypes = userPreferences.dietTypes.first()
        val allergens = userPreferences.allergens.first()
        val cuisines = userPreferences.cuisines.first()
        val maxCookingTime = userPreferences.maxCookingTime.first()
        val maxCalories = userPreferences.maxCalories.first()

        return UserContext(
            dietTypes = dietTypes.toList(),
            allergens = allergens.toList(),
            cuisines = cuisines.toList(),
            availableIngredients = ingredients,
            maxCookingTime = maxCookingTime,
            maxCalories = maxCalories
        )
    }

    /**
     * Get only allergens as a list (for quick filtering)
     */
    suspend fun getAllergens(): List<String> {
        return userPreferences.allergens.first().toList()
    }

    /**
     * Check if user has any allergens set
     */
    suspend fun hasAllergens(): Boolean {
        return userPreferences.allergens.first().isNotEmpty()
    }

    /**
     * Get diet types
     */
    suspend fun getDietTypes(): List<String> {
        return userPreferences.dietTypes.first().toList()
    }

    /**
     * Get max cooking time preference
     */
    suspend fun getMaxCookingTime(): Int? {
        return userPreferences.maxCookingTime.first()
    }

    /**
     * Get max calories preference
     */
    suspend fun getMaxCalories(): Int? {
        return userPreferences.maxCalories.first()
    }
}
