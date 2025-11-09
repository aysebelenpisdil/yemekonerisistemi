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
import com.yemekonerisistemi.app.data.prefs.UserPreferences
import com.yemekonerisistemi.app.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        // Diet chips
        binding.chipVegetarian.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDietType("Vejetaryen", isChecked)
        }
        binding.chipVegan.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDietType("Vegan", isChecked)
        }
        binding.chipGlutenFree.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDietType("Glutensiz", isChecked)
        }
        binding.chipKeto.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDietType("Keto", isChecked)
        }

        // Allergen chips
        binding.chipNuts.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllergen("Fındık/Fıstık", isChecked)
        }
        binding.chipMilk.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllergen("Süt", isChecked)
        }
        binding.chipEgg.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllergen("Yumurta", isChecked)
        }
        binding.chipSeafood.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllergen("Deniz Ürünleri", isChecked)
        }

        // Cuisine chips
        binding.chipTurkish.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCuisine("Türk", isChecked)
        }
        binding.chipItalian.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCuisine("İtalyan", isChecked)
        }
        binding.chipAsian.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCuisine("Asya", isChecked)
        }
        binding.chipMediterranean.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCuisine("Akdeniz", isChecked)
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
                binding.chipVegetarian.isChecked = dietTypes.contains("Vejetaryen")
                binding.chipVegan.isChecked = dietTypes.contains("Vegan")
                binding.chipGlutenFree.isChecked = dietTypes.contains("Glutensiz")
                binding.chipKeto.isChecked = dietTypes.contains("Keto")
            }
        }

        lifecycleScope.launch {
            viewModel.allergens.collect { allergens ->
                binding.chipNuts.isChecked = allergens.contains("Fındık/Fıstık")
                binding.chipMilk.isChecked = allergens.contains("Süt")
                binding.chipEgg.isChecked = allergens.contains("Yumurta")
                binding.chipSeafood.isChecked = allergens.contains("Deniz Ürünleri")
            }
        }

        lifecycleScope.launch {
            viewModel.cuisines.collect { cuisines ->
                binding.chipTurkish.isChecked = cuisines.contains("Türk")
                binding.chipItalian.isChecked = cuisines.contains("İtalyan")
                binding.chipAsian.isChecked = cuisines.contains("Asya")
                binding.chipMediterranean.isChecked = cuisines.contains("Akdeniz")
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