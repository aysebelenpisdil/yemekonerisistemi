package com.yemekonerisistemi.app.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Ingredient
import com.yemekonerisistemi.app.models.Recipe

/**
 * Tarif Detay Fragment
 * Spec Kit'e göre: Kapsamlı tarif görüntüleme, malzeme durumu, RAG açıklaması
 */
class RecipeDetailFragment : Fragment() {

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

    // Data
    private var currentRecipe: Recipe? = null
    private var isFavorite = false

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
        loadRecipeDetail()
        setupIngredients()
        setupInstructions()
        setupButtons()
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

    private fun loadRecipeDetail() {
        // TODO: Backend'den recipeId'ye göre çek
        // Şimdilik demo data
        currentRecipe = getDemoRecipe()

        currentRecipe?.let { recipe ->
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
        }
    }

    private fun setupIngredients() {
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(context)

        val ingredients = getRecipeIngredients()
        ingredientAdapter = IngredientDetailAdapter(ingredients)
        ingredientsRecyclerView.adapter = ingredientAdapter

        // Eksik malzeme sayısını hesapla
        val missingCount = ingredients.count { !it.isAvailable }
        if (missingCount > 0) {
            addToShoppingListButton.text = "🛒 Eksik Malzemeleri Ekle ($missingCount)"
            addToShoppingListButton.visibility = View.VISIBLE
        } else {
            addToShoppingListButton.visibility = View.GONE
        }
    }

    private fun setupInstructions() {
        instructionsRecyclerView.layoutManager = LinearLayoutManager(context)

        val instructions = getRecipeInstructions()
        instructionAdapter = InstructionAdapter(instructions)
        instructionsRecyclerView.adapter = instructionAdapter
    }

    private fun setupButtons() {
        // Favorilere ekle
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                favoriteButton.text = "💙 Favorilerimde"
                Toast.makeText(context, getString(R.string.favorite_added), Toast.LENGTH_SHORT).show()
            } else {
                favoriteButton.text = "❤️ Favorilere Ekle"
                Toast.makeText(context, getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show()
            }
        }

        // Paylaş
        shareButton.setOnClickListener {
            shareRecipe()
        }

        // Alışveriş listesine ekle
        addToShoppingListButton.setOnClickListener {
            addMissingIngredientsToShoppingList()
        }

        // Pişirmeye başla
        startCookingFab.setOnClickListener {
            startCookingMode()
        }
    }

    private fun shareRecipe() {
        currentRecipe?.let { recipe ->
            val shareText = """
                ${recipe.title}

                🕐 ${recipe.cookingTime} dakika
                🔥 ${recipe.calories} kalori

                ${recipe.recommendationReason}

                Yemek Öneri Sistemi ile paylaşıldı.
            """.trimIndent()

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            startActivity(Intent.createChooser(intent, "Tarifi Paylaş"))
        }
    }

    private fun addMissingIngredientsToShoppingList() {
        // TODO: Backend'e eksik malzemeleri gönder
        Toast.makeText(
            context,
            "Eksik malzemeler alışveriş listesine eklendi!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startCookingMode() {
        // TODO: Adım adım pişirme modu ekranına geç
        Toast.makeText(
            context,
            "Pişirme modu yakında! 👨‍🍳",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Demo Data Functions
    private fun getDemoRecipe(): Recipe {
        return Recipe(
            id = 1,
            title = "Tavuk Sote",
            cookingTime = 30,
            calories = 280,
            servings = 4,
            recommendationReason = "Bu tarif envanterinizdeki tavuk, domates ve biber ile mükemmel uyum sağlıyor. " +
                    "Ayrıca günlük kalori hedefinize uygun ve protein değeri yüksek. " +
                    "Hazırlanması kolay ve 30 dakikada hazır!",
            availableIngredients = "Tavuk, Domates, Biber",
            imageUrl = "",
            instructions = getRecipeInstructions()
        )
    }

    private fun getRecipeIngredients(): List<Ingredient> {
        return listOf(
            Ingredient(1, "Tavuk Göğsü", "500", "gram", "Et", isAvailable = true),
            Ingredient(2, "Domates", "3", "adet", "Sebze", isAvailable = true),
            Ingredient(3, "Yeşil Biber", "2", "adet", "Sebze", isAvailable = true),
            Ingredient(4, "Soğan", "1", "adet", "Sebze", isAvailable = true),
            Ingredient(5, "Sıvı Yağ", "2", "yemek kaşığı", "Yağ", isAvailable = false),
            Ingredient(6, "Tuz", "1", "çay kaşığı", "Baharat", isAvailable = true),
            Ingredient(7, "Karabiber", "1", "çay kaşığı", "Baharat", isAvailable = false)
        )
    }

    private fun getRecipeInstructions(): List<String> {
        return listOf(
            "Tavuk göğüslerini küp şeklinde doğrayın ve tuzlayın.",
            "Domatesleri ve biberleri küp şeklinde doğrayın.",
            "Soğanı ince ince doğrayın.",
            "Tavada sıvı yağı kızdırın ve tavukları ekleyin.",
            "Tavuklar renk alana kadar kavurun (yaklaşık 5-7 dakika).",
            "Soğanları ekleyip pembeleşene kadar kavurun.",
            "Domatesleri ve biberleri ekleyin.",
            "Kapağını kapatıp kısık ateşte sebzeler yumuşayana kadar pişirin (15-20 dakika).",
            "Tuz ve karabiberle tatlandırın.",
            "Sıcak servis yapın. Afiyet olsun!"
        )
    }
}
