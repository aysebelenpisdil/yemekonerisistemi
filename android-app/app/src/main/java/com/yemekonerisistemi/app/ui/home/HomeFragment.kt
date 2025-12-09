package com.yemekonerisistemi.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe
import com.yemekonerisistemi.app.ui.recipes.RecipeAdapter
import kotlinx.coroutines.launch

/**
 * Ana Sayfa / Dashboard Fragment
 * Spec Kit'e göre: Kişiselleştirilmiş öneriler, hızlı erişim, envanter özeti
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var inventoryCountText: TextView
    private lateinit var manageInventoryButton: MaterialButton
    private lateinit var inventorySummaryCard: MaterialCardView

    // Hızlı erişim kartları
    private lateinit var findRecipesCard: MaterialCardView
    private lateinit var mealPlannerCard: MaterialCardView
    private lateinit var shoppingListCard: MaterialCardView

    // RecyclerViews
    private lateinit var recommendedRecipesRecyclerView: RecyclerView
    private lateinit var trendingRecipesRecyclerView: RecyclerView

    private lateinit var recommendedAdapter: RecipeAdapter
    private lateinit var trendingAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupInventorySummary()
        setupQuickActions()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun initViews(view: View) {
        // Envanter özeti
        inventoryCountText = view.findViewById(R.id.inventoryCountText)
        manageInventoryButton = view.findViewById(R.id.manageInventoryButton)
        inventorySummaryCard = view.findViewById(R.id.inventorySummaryCard)

        // Hızlı erişim
        findRecipesCard = view.findViewById(R.id.findRecipesCard)
        mealPlannerCard = view.findViewById(R.id.mealPlannerCard)
        shoppingListCard = view.findViewById(R.id.shoppingListCard)

        // RecyclerViews
        recommendedRecipesRecyclerView = view.findViewById(R.id.recommendedRecipesRecyclerView)
        trendingRecipesRecyclerView = view.findViewById(R.id.trendingRecipesRecyclerView)

        // Tümünü gör butonları
        view.findViewById<TextView>(R.id.seeAllRecommendations).setOnClickListener {
            navigateToRecipeList("recommended")
        }

        view.findViewById<TextView>(R.id.seeAllTrending).setOnClickListener {
            navigateToRecipeList("trending")
        }
    }

    private fun setupInventorySummary() {
        // TODO: Backend'den envanter sayısını çek
        val inventoryCount = 12 // Demo data

        inventoryCountText.text = getString(R.string.home_ingredients_available, inventoryCount)

        manageInventoryButton.setOnClickListener {
            navigateToInventory()
        }

        inventorySummaryCard.setOnClickListener {
            navigateToInventory()
        }
    }

    private fun setupQuickActions() {
        findRecipesCard.setOnClickListener {
            navigateToRecipeList("all")
        }

        mealPlannerCard.setOnClickListener {
            // TODO: Yemek planlayıcı ekranına git
            Toast.makeText(context, "Yemek Planlayıcı yakında!", Toast.LENGTH_SHORT).show()
        }

        shoppingListCard.setOnClickListener {
            // TODO: Alışveriş listesi ekranına git
            Toast.makeText(context, "Alışveriş Listesi yakında!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViews() {
        // Önerilen tarifler - Horizontal layout
        recommendedRecipesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recommendedAdapter = RecipeAdapter(emptyList()) { recipe ->
            navigateToRecipeDetail(recipe)
        }
        recommendedRecipesRecyclerView.adapter = recommendedAdapter

        // Popüler tarifler - Horizontal layout
        trendingRecipesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = RecipeAdapter(emptyList()) { recipe ->
            navigateToRecipeDetail(recipe)
        }
        trendingRecipesRecyclerView.adapter = trendingAdapter
    }

    private fun observeViewModel() {
        // Envanter sayısı
        lifecycleScope.launch {
            viewModel.inventoryCount.collect { count ->
                inventoryCountText.text = getString(R.string.home_ingredients_available, count)
            }
        }

        // Önerilen tarifler
        lifecycleScope.launch {
            viewModel.recommendedRecipes.collect { recipes ->
                recommendedAdapter = RecipeAdapter(recipes) { recipe ->
                    navigateToRecipeDetail(recipe)
                }
                recommendedRecipesRecyclerView.adapter = recommendedAdapter
            }
        }

        // Popüler tarifler
        lifecycleScope.launch {
            viewModel.trendingRecipes.collect { recipes ->
                trendingAdapter = RecipeAdapter(recipes) { recipe ->
                    navigateToRecipeDetail(recipe)
                }
                trendingRecipesRecyclerView.adapter = trendingAdapter
            }
        }

        // Hata durumu
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.error?.let { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }

    // Navigation helpers
    private fun navigateToInventory() {
        // MainActivity'deki bottom navigation ile inventory'ye geç
        requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            R.id.bottom_navigation
        )?.selectedItemId = R.id.navigation_inventory
    }

    private fun navigateToRecipeList(filter: String) {
        // MainActivity'deki bottom navigation ile recipes'e geç
        requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            R.id.bottom_navigation
        )?.selectedItemId = R.id.navigation_recipes
    }

    private fun navigateToRecipeDetail(recipe: Recipe) {
        // TODO: Recipe detail navigation implement edilecek
        Toast.makeText(context, "Tarif detayı: ${recipe.title}", Toast.LENGTH_SHORT).show()
    }
}
