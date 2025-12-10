package com.yemekonerisistemi.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * User context model for personalized recipe recommendations
 * Sent to backend API to apply filtering and customize LLM responses
 */
data class UserContext(
    @SerializedName("diet_types")
    val dietTypes: List<String> = emptyList(),

    @SerializedName("allergens")
    val allergens: List<String> = emptyList(),

    @SerializedName("cuisines")
    val cuisines: List<String> = emptyList(),

    @SerializedName("available_ingredients")
    val availableIngredients: List<String> = emptyList(),

    @SerializedName("max_cooking_time")
    val maxCookingTime: Int? = null,

    @SerializedName("max_calories")
    val maxCalories: Int? = null,

    @SerializedName("cooking_time_preferences")
    val cookingTimePreferences: List<String> = emptyList(),

    @SerializedName("calorie_preferences")
    val caloriePreferences: List<String> = emptyList(),

    @SerializedName("skill_levels")
    val skillLevels: List<String> = emptyList(),

    @SerializedName("spice_preferences")
    val spicePreferences: List<String> = emptyList(),

    @SerializedName("serving_sizes")
    val servingSizes: List<String> = emptyList(),

    @SerializedName("meal_types")
    val mealTypes: List<String> = emptyList()
) {
    /**
     * Check if user has any dietary restrictions (allergens or diet types)
     */
    fun hasRestrictions(): Boolean = allergens.isNotEmpty() || dietTypes.isNotEmpty()

    /**
     * Check if user has any preferences set
     */
    fun hasPreferences(): Boolean = dietTypes.isNotEmpty() ||
            allergens.isNotEmpty() ||
            cuisines.isNotEmpty() ||
            maxCookingTime != null ||
            maxCalories != null ||
            cookingTimePreferences.isNotEmpty() ||
            caloriePreferences.isNotEmpty() ||
            skillLevels.isNotEmpty() ||
            spicePreferences.isNotEmpty() ||
            servingSizes.isNotEmpty() ||
            mealTypes.isNotEmpty()

    companion object {
        /**
         * Create an empty UserContext with no preferences
         */
        fun empty(): UserContext = UserContext()
    }
}
