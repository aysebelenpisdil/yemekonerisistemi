package com.yemekonerisistemi.app.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    // Keys
    companion object {
        val DIET_TYPES_KEY = stringSetPreferencesKey("diet_types")
        val ALLERGENS_KEY = stringSetPreferencesKey("allergens")
        val CUISINES_KEY = stringSetPreferencesKey("cuisines")
        val MAX_COOKING_TIME_KEY = intPreferencesKey("max_cooking_time")
        val MAX_CALORIES_KEY = intPreferencesKey("max_calories")
        val COOKING_TIME_PREFERENCES_KEY = stringSetPreferencesKey("cooking_time_preferences")
        val CALORIE_PREFERENCES_KEY = stringSetPreferencesKey("calorie_preferences")
        val SKILL_LEVELS_KEY = stringSetPreferencesKey("skill_levels")
        val SPICE_PREFERENCES_KEY = stringSetPreferencesKey("spice_preferences")
        val SERVING_SIZES_KEY = stringSetPreferencesKey("serving_sizes")
        val MEAL_TYPES_KEY = stringSetPreferencesKey("meal_types")
        val NOTIFICATIONS_RECOMMENDATIONS_KEY = booleanPreferencesKey("notifications_recommendations")
        val NOTIFICATIONS_INVENTORY_KEY = booleanPreferencesKey("notifications_inventory")
        val NOTIFICATIONS_WEEKLY_SUMMARY_KEY = booleanPreferencesKey("notifications_weekly_summary")
    }

    // Diet Types
    val dietTypes: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DIET_TYPES_KEY] ?: emptySet()
        }

    suspend fun updateDietTypes(dietTypes: Set<String>) {
        dataStore.edit { preferences ->
            preferences[DIET_TYPES_KEY] = dietTypes
        }
    }

    // Allergens
    val allergens: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[ALLERGENS_KEY] ?: emptySet()
        }

    suspend fun updateAllergens(allergens: Set<String>) {
        dataStore.edit { preferences ->
            preferences[ALLERGENS_KEY] = allergens
        }
    }

    // Cuisines
    val cuisines: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[CUISINES_KEY] ?: emptySet()
        }

    suspend fun updateCuisines(cuisines: Set<String>) {
        dataStore.edit { preferences ->
            preferences[CUISINES_KEY] = cuisines
        }
    }

    // Max Cooking Time (minutes, null = no limit)
    val maxCookingTime: Flow<Int?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[MAX_COOKING_TIME_KEY]
        }

    suspend fun updateMaxCookingTime(minutes: Int?) {
        dataStore.edit { preferences ->
            if (minutes != null) {
                preferences[MAX_COOKING_TIME_KEY] = minutes
            } else {
                preferences.remove(MAX_COOKING_TIME_KEY)
            }
        }
    }

    // Max Calories (null = no limit)
    val maxCalories: Flow<Int?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[MAX_CALORIES_KEY]
        }

    suspend fun updateMaxCalories(calories: Int?) {
        dataStore.edit { preferences ->
            if (calories != null) {
                preferences[MAX_CALORIES_KEY] = calories
            } else {
                preferences.remove(MAX_CALORIES_KEY)
            }
        }
    }

    // Cooking Time Preferences (chip-based)
    val cookingTimePreferences: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[COOKING_TIME_PREFERENCES_KEY] ?: emptySet()
        }

    suspend fun updateCookingTimePreferences(preferences: Set<String>) {
        dataStore.edit { prefs ->
            prefs[COOKING_TIME_PREFERENCES_KEY] = preferences
        }
    }

    // Calorie Preferences (chip-based)
    val caloriePreferences: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[CALORIE_PREFERENCES_KEY] ?: emptySet()
        }

    suspend fun updateCaloriePreferences(preferences: Set<String>) {
        dataStore.edit { prefs ->
            prefs[CALORIE_PREFERENCES_KEY] = preferences
        }
    }

    // Skill Levels
    val skillLevels: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SKILL_LEVELS_KEY] ?: emptySet()
        }

    suspend fun updateSkillLevels(skillLevels: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SKILL_LEVELS_KEY] = skillLevels
        }
    }

    // Spice Preferences
    val spicePreferences: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SPICE_PREFERENCES_KEY] ?: emptySet()
        }

    suspend fun updateSpicePreferences(spicePreferences: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SPICE_PREFERENCES_KEY] = spicePreferences
        }
    }

    // Serving Sizes
    val servingSizes: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SERVING_SIZES_KEY] ?: emptySet()
        }

    suspend fun updateServingSizes(servingSizes: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SERVING_SIZES_KEY] = servingSizes
        }
    }

    // Meal Types
    val mealTypes: Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[MEAL_TYPES_KEY] ?: emptySet()
        }

    suspend fun updateMealTypes(mealTypes: Set<String>) {
        dataStore.edit { preferences ->
            preferences[MEAL_TYPES_KEY] = mealTypes
        }
    }

    // Notifications - Recommendations
    val notificationsRecommendations: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_RECOMMENDATIONS_KEY] ?: false
        }

    suspend fun updateNotificationsRecommendations(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_RECOMMENDATIONS_KEY] = enabled
        }
    }

    // Notifications - Inventory
    val notificationsInventory: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_INVENTORY_KEY] ?: false
        }

    suspend fun updateNotificationsInventory(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_INVENTORY_KEY] = enabled
        }
    }

    // Notifications - Weekly Summary
    val notificationsWeeklySummary: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_WEEKLY_SUMMARY_KEY] ?: false
        }

    suspend fun updateNotificationsWeeklySummary(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_WEEKLY_SUMMARY_KEY] = enabled
        }
    }

    // Clear all preferences
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}