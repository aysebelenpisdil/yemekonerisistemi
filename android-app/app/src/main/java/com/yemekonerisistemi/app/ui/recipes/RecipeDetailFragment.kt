package com.yemekonerisistemi.app.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Ingredient
import com.yemekonerisistemi.app.models.Recipe
import kotlinx.coroutines.launch

/**
 * Tarif Detay Fragment
 * Spec Kit'e göre: Kapsamlı tarif görüntüleme, malzeme durumu, RAG açıklaması
 */
class RecipeDetailFragment : Fragment() {

    private val viewModel: RecipeDetailViewModel by viewModels()

    // UI Elements
    private lateinit var recipeTitleText: TextView
    private lateinit var cookingTimeText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var servingsText: TextView
    private lateinit var recommendationReasonText: TextView

    // Nutrition
    private lateinit var nutritionCaloriesText: TextView
    private lateinit var nutritionProteinText: TextView
    private lateinit var nutritionCarbsText: TextView
    private lateinit var nutritionFatText: TextView

    // Buttons
    private lateinit var favoriteButton: MaterialButton
    private lateinit var shareButton: MaterialButton
    private lateinit var addToShoppingListButton: MaterialButton
    private lateinit var startCookingFab: ExtendedFloatingActionButton

    // RecyclerViews
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var instructionsRecyclerView: RecyclerView

    // Adapters
    private lateinit var ingredientAdapter: IngredientDetailAdapter
    private lateinit var instructionAdapter: InstructionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViews()
        setupButtons()
        observeViewModel()

        // Demo için recipeId 1 yükle
        viewModel.loadRecipeDetail(1)
    }

    private fun initViews(view: View) {
        // Text views
        recipeTitleText = view.findViewById(R.id.recipeTitleText)
        cookingTimeText = view.findViewById(R.id.cookingTimeText)
        caloriesText = view.findViewById(R.id.caloriesText)
        servingsText = view.findViewById(R.id.servingsText)
        recommendationReasonText = view.findViewById(R.id.recommendationReasonText)

        // Nutrition
        nutritionCaloriesText = view.findViewById(R.id.nutritionCaloriesText)
        nutritionProteinText = view.findViewById(R.id.nutritionProteinText)
        nutritionCarbsText = view.findViewById(R.id.nutritionCarbsText)
        nutritionFatText = view.findViewById(R.id.nutritionFatText)

        // Buttons
        favoriteButton = view.findViewById(R.id.favoriteButton)
        shareButton = view.findViewById(R.id.shareButton)
        addToShoppingListButton = view.findViewById(R.id.addToShoppingListButton)
        startCookingFab = view.findViewById(R.id.startCookingFab)

        // RecyclerViews
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView)
        instructionsRecyclerView = view.findViewById(R.id.instructionsRecyclerView)
    }

    private fun setupRecyclerViews() {
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(context)
        ingredientAdapter = IngredientDetailAdapter(emptyList())
        ingredientsRecyclerView.adapter = ingredientAdapter

        instructionsRecyclerView.layoutManager = LinearLayoutManager(context)
        instructionAdapter = InstructionAdapter(emptyList())
        instructionsRecyclerView.adapter = instructionAdapter
    }

    private fun setupButtons() {
        // Favorilere ekle
        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        // Paylaş
        shareButton.setOnClickListener {
            shareRecipe()
        }

        // Alışveriş listesine ekle
        addToShoppingListButton.setOnClickListener {
            viewModel.addMissingToShoppingList()
        }

        // Pişirmeye başla
        startCookingFab.setOnClickListener {
            viewModel.startCookingMode()
        }
    }

    private fun observeViewModel() {
        // Tarif detayı
        lifecycleScope.launch {
            viewModel.recipe.collect { recipe ->
                recipe?.let { updateRecipeUI(it) }
            }
        }

        // Malzeme listesi
        lifecycleScope.launch {
            viewModel.ingredients.collect { ingredients ->
                ingredientAdapter = IngredientDetailAdapter(ingredients)
                ingredientsRecyclerView.adapter = ingredientAdapter
            }
        }

        // Favori durumu
        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    favoriteButton.text = "💙 Favorilerimde"
                } else {
                    favoriteButton.text = "❤️ Favorilere Ekle"
                }
            }
        }

        // Eksik malzeme sayısı
        lifecycleScope.launch {
            viewModel.missingIngredientsCount.collect { count ->
                if (count > 0) {
                    addToShoppingListButton.text = "🛒 Eksik Malzemeleri Ekle ($count)"
                    addToShoppingListButton.visibility = View.VISIBLE
                } else {
                    addToShoppingListButton.visibility = View.GONE
                }
            }
        }

        // UI durumu
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Hata mesajı
                state.error?.let { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }

                // Aksiyon sonuçları
                state.lastAction?.let { action ->
                    val message = when (action) {
                        is DetailAction.AddedToFavorites -> getString(R.string.favorite_added)
                        is DetailAction.RemovedFromFavorites -> getString(R.string.favorite_removed)
                        is DetailAction.AddedToShoppingList -> "Eksik malzemeler alışveriş listesine eklendi!"
                        is DetailAction.CookingModeStarted -> "Pişirme modu yakında! 👨‍🍳"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    viewModel.clearLastAction()
                }
            }
        }
    }

    private fun updateRecipeUI(recipe: Recipe) {
        recipeTitleText.text = recipe.title
        cookingTimeText.text = getString(R.string.cooking_time_format, recipe.cookingTime)
        caloriesText.text = getString(R.string.calorie_format, recipe.calories)
        servingsText.text = getString(R.string.servings_format, recipe.servings)
        recommendationReasonText.text = recipe.recommendationReason

        // Nutrition (demo data)
        nutritionCaloriesText.text = "${recipe.calories} kcal"
        nutritionProteinText.text = "32g"
        nutritionCarbsText.text = "15g"
        nutritionFatText.text = "12g"

        // Talimatlar
        instructionAdapter = InstructionAdapter(recipe.instructions)
        instructionsRecyclerView.adapter = instructionAdapter
    }

    private fun shareRecipe() {
        val shareText = viewModel.getShareText()
        if (shareText.isNotEmpty()) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Tarifi Paylaş"))
        }
    }
}
