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
    val maxCalories: Int? = null
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
            maxCalories != null

    companion object {
        /**
         * Create an empty UserContext with no preferences
         */
        fun empty(): UserContext = UserContext()
    }
}
