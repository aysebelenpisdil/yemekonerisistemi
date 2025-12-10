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

    // Cooking time preferences (chip-based)
    private val _cookingTimePreferences = MutableStateFlow(setOf<String>())
    val cookingTimePreferences: StateFlow<Set<String>> = _cookingTimePreferences.asStateFlow()

    // Calorie preferences (chip-based)
    private val _caloriePreferences = MutableStateFlow(setOf<String>())
    val caloriePreferences: StateFlow<Set<String>> = _caloriePreferences.asStateFlow()

    // Skill levels
    private val _skillLevels = MutableStateFlow(setOf<String>())
    val skillLevels: StateFlow<Set<String>> = _skillLevels.asStateFlow()

    // Spice preferences
    private val _spicePreferences = MutableStateFlow(setOf<String>())
    val spicePreferences: StateFlow<Set<String>> = _spicePreferences.asStateFlow()

    // Serving sizes
    private val _servingSizes = MutableStateFlow(setOf<String>())
    val servingSizes: StateFlow<Set<String>> = _servingSizes.asStateFlow()

    // Meal types
    private val _mealTypes = MutableStateFlow(setOf<String>())
    val mealTypes: StateFlow<Set<String>> = _mealTypes.asStateFlow()

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
            userPreferences.cookingTimePreferences.collect { prefs ->
                _cookingTimePreferences.value = prefs
            }
        }

        viewModelScope.launch {
            userPreferences.caloriePreferences.collect { prefs ->
                _caloriePreferences.value = prefs
            }
        }

        viewModelScope.launch {
            userPreferences.skillLevels.collect { levels ->
                _skillLevels.value = levels
            }
        }

        viewModelScope.launch {
            userPreferences.spicePreferences.collect { prefs ->
                _spicePreferences.value = prefs
            }
        }

        viewModelScope.launch {
            userPreferences.servingSizes.collect { sizes ->
                _servingSizes.value = sizes
            }
        }

        viewModelScope.launch {
            userPreferences.mealTypes.collect { types ->
                _mealTypes.value = types
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

    // Update functions for set-based preferences
    fun updateDietTypes(dietTypes: Set<String>) {
        viewModelScope.launch {
            _dietTypes.value = dietTypes
            userPreferences.updateDietTypes(dietTypes)
        }
    }

    fun updateAllergens(allergens: Set<String>) {
        viewModelScope.launch {
            _allergens.value = allergens
            userPreferences.updateAllergens(allergens)
        }
    }

    fun updateCuisines(cuisines: Set<String>) {
        viewModelScope.launch {
            _cuisines.value = cuisines
            userPreferences.updateCuisines(cuisines)
        }
    }

    fun updateCookingTimePreferences(prefs: Set<String>) {
        viewModelScope.launch {
            _cookingTimePreferences.value = prefs
            userPreferences.updateCookingTimePreferences(prefs)
        }
    }

    fun updateCaloriePreferences(prefs: Set<String>) {
        viewModelScope.launch {
            _caloriePreferences.value = prefs
            userPreferences.updateCaloriePreferences(prefs)
        }
    }

    fun updateSkillLevels(levels: Set<String>) {
        viewModelScope.launch {
            _skillLevels.value = levels
            userPreferences.updateSkillLevels(levels)
        }
    }

    fun updateSpicePreferences(prefs: Set<String>) {
        viewModelScope.launch {
            _spicePreferences.value = prefs
            userPreferences.updateSpicePreferences(prefs)
        }
    }

    fun updateServingSizes(sizes: Set<String>) {
        viewModelScope.launch {
            _servingSizes.value = sizes
            userPreferences.updateServingSizes(sizes)
        }
    }

    fun updateMealTypes(types: Set<String>) {
        viewModelScope.launch {
            _mealTypes.value = types
            userPreferences.updateMealTypes(types)
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