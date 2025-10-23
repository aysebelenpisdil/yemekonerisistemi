package com.yemekonerisistemi.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe
import com.yemekonerisistemi.app.ui.recipes.RecipeAdapter

/**
 * Ana Sayfa / Dashboard Fragment
 * Spec Kit'e göre: Kişiselleştirilmiş öneriler, hızlı erişim, envanter özeti
 */
class HomeFragment : Fragment() {

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
        setupRecommendedRecipes()
        setupTrendingRecipes()
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

    private fun setupRecommendedRecipes() {
        // Horizontal layout manager
        recommendedRecipesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Önerilen tarifler (demo data)
        val recommendedRecipes = getRecommendedRecipes()

        recommendedAdapter = RecipeAdapter(recommendedRecipes) { recipe ->
            navigateToRecipeDetail(recipe)
        }

        recommendedRecipesRecyclerView.adapter = recommendedAdapter
    }

    private fun setupTrendingRecipes() {
        // Horizontal layout manager
        trendingRecipesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Popüler tarifler (demo data)
        val trendingRecipes = getTrendingRecipes()

        trendingAdapter = RecipeAdapter(trendingRecipes) { recipe ->
            navigateToRecipeDetail(recipe)
        }

        trendingRecipesRecyclerView.adapter = trendingAdapter
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

    // Demo data - Gerçekte backend'den gelecek
    private fun getRecommendedRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Tavuk Sote",
                cookingTime = 30,
                calories = 280,
                recommendationReason = "Envanterindeki tavuk ve sebzelerle mükemmel uyum!",
                availableIngredients = "Tavuk, Domates, Biber",
                imageUrl = ""
            ),
            Recipe(
                id = 2,
                title = "Menemen",
                cookingTime = 15,
                calories = 220,
                recommendationReason = "Yumurta ve domateslerinle hızlı kahvaltı!",
                availableIngredients = "Yumurta, Domates, Biber",
                imageUrl = ""
            ),
            Recipe(
                id = 3,
                title = "Sebze Çorbası",
                cookingTime = 25,
                calories = 150,
                recommendationReason = "Sebzelerini değerlendirmek için ideal",
                availableIngredients = "Domates, Soğan, Biber",
                imageUrl = ""
            )
        )
    }

    private fun getTrendingRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 4,
                title = "Kuru Fasulye",
                cookingTime = 90,
                calories = 350,
                recommendationReason = "Bu hafta en çok yapılan tarif!",
                availableIngredients = "Fasulye, Soğan",
                imageUrl = ""
            ),
            Recipe(
                id = 5,
                title = "Mercimek Çorbası",
                cookingTime = 35,
                calories = 180,
                recommendationReason = "Klasik lezzet, her zaman favoride",
                availableIngredients = "Mercimek, Soğan",
                imageUrl = ""
            ),
            Recipe(
                id = 6,
                title = "Makarna",
                cookingTime = 20,
                calories = 400,
                recommendationReason = "Hızlı ve pratik!",
                availableIngredients = "Makarna",
                imageUrl = ""
            )
        )
    }
}
