package com.yemekonerisistemi.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yemekonerisistemi.app.data.prefs.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    // Diet types
    private val _dietTypes = MutableStateFlow(setOf<String>())
    val dietTypes: StateFlow<Set<String>> = _dietTypes.asStateFlow()

    // Allergens
    private val _allergens = MutableStateFlow(setOf<String>())
    val allergens: StateFlow<Set<String>> = _allergens.asStateFlow()

    // Cuisines
    private val _cuisines = MutableStateFlow(setOf<String>())
    val cuisines: StateFlow<Set<String>> = _cuisines.asStateFlow()

    // Notification settings
    private val _notificationSettings = MutableStateFlow(mapOf<String, Boolean>())
    val notificationSettings: StateFlow<Map<String, Boolean>> = _notificationSettings.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferences.dietTypes.collect { dietTypes ->
                _dietTypes.value = dietTypes
            }
        }

        viewModelScope.launch {
            userPreferences.allergens.collect { allergens ->
                _allergens.value = allergens
            }
        }

        viewModelScope.launch {
            userPreferences.cuisines.collect { cuisines ->
                _cuisines.value = cuisines
            }
        }

        viewModelScope.launch {
            userPreferences.notificationsRecommendations.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value + ("recommendations" to enabled)
            }
        }

        viewModelScope.launch {
            userPreferences.notificationsInventory.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value + ("inventory" to enabled)
            }
        }

        viewModelScope.launch {
            userPreferences.notificationsWeeklySummary.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value + ("weeklySummary" to enabled)
            }
        }
    }

    fun updateDietType(dietType: String, isSelected: Boolean) {
        viewModelScope.launch {
            val currentDietTypes = _dietTypes.value.toMutableSet()
            if (isSelected) {
                currentDietTypes.add(dietType)
            } else {
                currentDietTypes.remove(dietType)
            }
            _dietTypes.value = currentDietTypes
            userPreferences.updateDietTypes(currentDietTypes)
        }
    }

    fun updateAllergen(allergen: String, isSelected: Boolean) {
        viewModelScope.launch {
            val currentAllergens = _allergens.value.toMutableSet()
            if (isSelected) {
                currentAllergens.add(allergen)
            } else {
                currentAllergens.remove(allergen)
            }
            _allergens.value = currentAllergens
            userPreferences.updateAllergens(currentAllergens)
        }
    }

    fun updateCuisine(cuisine: String, isSelected: Boolean) {
        viewModelScope.launch {
            val currentCuisines = _cuisines.value.toMutableSet()
            if (isSelected) {
                currentCuisines.add(cuisine)
            } else {
                currentCuisines.remove(cuisine)
            }
            _cuisines.value = currentCuisines
            userPreferences.updateCuisines(currentCuisines)
        }
    }

    fun updateNotificationSetting(key: String, enabled: Boolean) {
        viewModelScope.launch {
            _notificationSettings.value = _notificationSettings.value + (key to enabled)
            when (key) {
                "recommendations" -> userPreferences.updateNotificationsRecommendations(enabled)
                "inventory" -> userPreferences.updateNotificationsInventory(enabled)
                "weeklySummary" -> userPreferences.updateNotificationsWeeklySummary(enabled)
            }
        }
    }
}