package com.yemekonerisistemi.app.data.local

import android.content.Context
import com.yemekonerisistemi.app.data.model.UserContext
import com.yemekonerisistemi.app.data.prefs.UserPreferences
import com.yemekonerisistemi.app.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

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
        // Combine basic preferences
        val basicPrefsFlow = combine(
            userPreferences.dietTypes,
            userPreferences.allergens,
            userPreferences.cuisines
        ) { dietTypes, allergens, cuisines ->
            Triple(dietTypes, allergens, cuisines)
        }

        // Combine legacy constraints with inventory
        val constraintsFlow = combine(
            userPreferences.maxCookingTime,
            userPreferences.maxCalories,
            inventoryRepository.getIngredientNames()
        ) { maxCookingTime, maxCalories, ingredients ->
            Triple(maxCookingTime, maxCalories, ingredients)
        }

        // Combine new chip-based preferences
        val newPrefsFlow1 = combine(
            userPreferences.cookingTimePreferences,
            userPreferences.caloriePreferences,
            userPreferences.skillLevels
        ) { cookingTime, calories, skills ->
            Triple(cookingTime, calories, skills)
        }

        val newPrefsFlow2 = combine(
            userPreferences.spicePreferences,
            userPreferences.servingSizes,
            userPreferences.mealTypes
        ) { spice, servings, meals ->
            Triple(spice, servings, meals)
        }

        // Combine all flows together
        return combine(basicPrefsFlow, constraintsFlow, newPrefsFlow1, newPrefsFlow2) { basic, constraints, new1, new2 ->
            UserContext(
                dietTypes = basic.first.toList(),
                allergens = basic.second.toList(),
                cuisines = basic.third.toList(),
                availableIngredients = constraints.third,
                maxCookingTime = constraints.first,
                maxCalories = constraints.second,
                cookingTimePreferences = new1.first.toList(),
                caloriePreferences = new1.second.toList(),
                skillLevels = new1.third.toList(),
                spicePreferences = new2.first.toList(),
                servingSizes = new2.second.toList(),
                mealTypes = new2.third.toList()
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
        val cookingTimePrefs = userPreferences.cookingTimePreferences.first()
        val caloriePrefs = userPreferences.caloriePreferences.first()
        val skillLevels = userPreferences.skillLevels.first()
        val spicePrefs = userPreferences.spicePreferences.first()
        val servingSizes = userPreferences.servingSizes.first()
        val mealTypes = userPreferences.mealTypes.first()

        return UserContext(
            dietTypes = dietTypes.toList(),
            allergens = allergens.toList(),
            cuisines = cuisines.toList(),
            availableIngredients = ingredients,
            maxCookingTime = maxCookingTime,
            maxCalories = maxCalories,
            cookingTimePreferences = cookingTimePrefs.toList(),
            caloriePreferences = caloriePrefs.toList(),
            skillLevels = skillLevels.toList(),
            spicePreferences = spicePrefs.toList(),
            servingSizes = servingSizes.toList(),
            mealTypes = mealTypes.toList()
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
