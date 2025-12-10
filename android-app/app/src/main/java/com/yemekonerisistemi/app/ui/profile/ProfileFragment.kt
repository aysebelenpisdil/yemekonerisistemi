package com.yemekonerisistemi.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.data.prefs.UserPreferences
import com.yemekonerisistemi.app.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Chip options for each preference category
    companion object {
        val DIET_TYPES = listOf(
            "Vejetaryen", "Vegan", "Glutensiz", "Keto",
            "Paleo", "Düşük Karbonhidrat", "Helal", "Pescetaryen"
        )

        val ALLERGENS = listOf(
            "Fındık/Fıstık", "Süt", "Yumurta", "Deniz Ürünleri",
            "Gluten", "Soya", "Susam", "Kereviz", "Hardal"
        )

        val CUISINES = listOf(
            "Türk", "İtalyan", "Asya", "Akdeniz",
            "Meksika", "Hint", "Japon", "Fransız", "Ortadoğu"
        )

        val COOKING_TIMES = listOf("15dk", "30dk", "45dk", "60dk", "90+dk")

        val CALORIES = listOf("300kcal", "500kcal", "750kcal", "1000kcal", "1500+kcal")

        val SKILL_LEVELS = listOf("Başlangıç", "Orta", "İleri")

        val SPICE_LEVELS = listOf("Acısız", "Az Acılı", "Orta", "Çok Acılı")

        val SERVING_SIZES = listOf("1-2 kişi", "3-4 kişi", "5+ kişi")

        val MEAL_TYPES = listOf("Kahvaltı", "Öğle", "Akşam", "Atıştırmalık")
    }

    // Provide factory to inject UserPreferences into ProfileViewModel
    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    val prefs = UserPreferences(requireContext().applicationContext)
                    @Suppress("UNCHECKED_CAST")
                    return ProfileViewModel(prefs) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUserInfo()
        setupPreferences()
        setupButtons()
        setupNotifications()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Profil"
    }

    private fun setupUserInfo() {
        // Placeholder user data
        binding.userEmailText.text = "kullanici@email.com"

        binding.logoutButton.setOnClickListener {
            Toast.makeText(context, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show()
            // TODO: Implement logout
        }
    }

    private fun setupPreferences() {
        // Diet Types
        binding.prefDietTypes.apply {
            setTitle(getString(R.string.pref_diet_types))
            setChips(DIET_TYPES)
            setOnSelectionChangedListener { selected ->
                viewModel.updateDietTypes(selected)
            }
        }

        // Allergens
        binding.prefAllergens.apply {
            setTitle(getString(R.string.pref_allergens))
            setChips(ALLERGENS)
            setOnSelectionChangedListener { selected ->
                viewModel.updateAllergens(selected)
            }
        }

        // Cuisines
        binding.prefCuisines.apply {
            setTitle(getString(R.string.pref_cuisines))
            setChips(CUISINES)
            setOnSelectionChangedListener { selected ->
                viewModel.updateCuisines(selected)
            }
        }

        // Cooking Time
        binding.prefCookingTime.apply {
            setTitle(getString(R.string.pref_cooking_time))
            setChips(COOKING_TIMES)
            setOnSelectionChangedListener { selected ->
                viewModel.updateCookingTimePreferences(selected)
            }
        }

        // Calories
        binding.prefCalories.apply {
            setTitle(getString(R.string.pref_calories))
            setChips(CALORIES)
            setOnSelectionChangedListener { selected ->
                viewModel.updateCaloriePreferences(selected)
            }
        }

        // Skill Levels
        binding.prefSkillLevels.apply {
            setTitle(getString(R.string.pref_skill_levels))
            setChips(SKILL_LEVELS)
            setOnSelectionChangedListener { selected ->
                viewModel.updateSkillLevels(selected)
            }
        }

        // Spice Preferences
        binding.prefSpice.apply {
            setTitle(getString(R.string.pref_spice))
            setChips(SPICE_LEVELS)
            setOnSelectionChangedListener { selected ->
                viewModel.updateSpicePreferences(selected)
            }
        }

        // Serving Sizes
        binding.prefServingSizes.apply {
            setTitle(getString(R.string.pref_serving_sizes))
            setChips(SERVING_SIZES)
            setOnSelectionChangedListener { selected ->
                viewModel.updateServingSizes(selected)
            }
        }

        // Meal Types
        binding.prefMealTypes.apply {
            setTitle(getString(R.string.pref_meal_types))
            setChips(MEAL_TYPES)
            setShowDivider(false) // Last item, no divider
            setOnSelectionChangedListener { selected ->
                viewModel.updateMealTypes(selected)
            }
        }
    }

    private fun setupButtons() {
        binding.myInventoryButton.setOnClickListener {
            Toast.makeText(context, "Buzdolabım açılıyor...", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to inventory
        }

        binding.shoppingListButton.setOnClickListener {
            Toast.makeText(context, "Alışveriş listesi açılıyor...", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to shopping list
        }

        binding.viewAllSavedButton.setOnClickListener {
            Toast.makeText(context, "Kaydedilenler açılıyor...", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to saved recipes
        }

        binding.downloadDataButton.setOnClickListener {
            Toast.makeText(context, "Veri indiriliyor...", Toast.LENGTH_SHORT).show()
            // TODO: Implement data download
        }

        binding.deleteDataButton.setOnClickListener {
            Toast.makeText(context, "Hesap silme işlemi...", Toast.LENGTH_SHORT).show()
            // TODO: Implement account deletion
        }

        binding.feedbackButton.setOnClickListener {
            Toast.makeText(context, "Geri bildirim formu açılıyor...", Toast.LENGTH_SHORT).show()
            // TODO: Open feedback form
        }
    }

    private fun setupNotifications() {
        binding.notificationsRecommendationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateNotificationSetting("recommendations", isChecked)
        }

        binding.notificationsInventorySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateNotificationSetting("inventory", isChecked)
        }

        binding.notificationsWeeklySummarySwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateNotificationSetting("weeklySummary", isChecked)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.dietTypes.collect { dietTypes ->
                binding.prefDietTypes.setSelectedItems(dietTypes)
            }
        }

        lifecycleScope.launch {
            viewModel.allergens.collect { allergens ->
                binding.prefAllergens.setSelectedItems(allergens)
            }
        }

        lifecycleScope.launch {
            viewModel.cuisines.collect { cuisines ->
                binding.prefCuisines.setSelectedItems(cuisines)
            }
        }

        lifecycleScope.launch {
            viewModel.cookingTimePreferences.collect { prefs ->
                binding.prefCookingTime.setSelectedItems(prefs)
            }
        }

        lifecycleScope.launch {
            viewModel.caloriePreferences.collect { prefs ->
                binding.prefCalories.setSelectedItems(prefs)
            }
        }

        lifecycleScope.launch {
            viewModel.skillLevels.collect { levels ->
                binding.prefSkillLevels.setSelectedItems(levels)
            }
        }

        lifecycleScope.launch {
            viewModel.spicePreferences.collect { prefs ->
                binding.prefSpice.setSelectedItems(prefs)
            }
        }

        lifecycleScope.launch {
            viewModel.servingSizes.collect { sizes ->
                binding.prefServingSizes.setSelectedItems(sizes)
            }
        }

        lifecycleScope.launch {
            viewModel.mealTypes.collect { types ->
                binding.prefMealTypes.setSelectedItems(types)
            }
        }

        lifecycleScope.launch {
            viewModel.notificationSettings.collect { settings ->
                binding.notificationsRecommendationsSwitch.isChecked =
                    settings["recommendations"] ?: false
                binding.notificationsInventorySwitch.isChecked =
                    settings["inventory"] ?: false
                binding.notificationsWeeklySummarySwitch.isChecked =
                    settings["weeklySummary"] ?: false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}