package com.yemekonerisistemi.app.ui.recipes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.yemekonerisistemi.app.R
import kotlinx.coroutines.launch

/**
 * Tarif Detay Fragment
 * Spec Kit'e göre: Kapsamlı tarif görüntüleme, malzeme durumu, RAG açıklaması
 */
class RecipeDetailFragment : Fragment() {

    private val viewModel: RecipeDetailViewModel by viewModels()

    // Views
    private var recipeImage: ImageView? = null
    private var recipeTitle: TextView? = null
    private var cookingTime: TextView? = null
    private var calories: TextView? = null
    private var servings: TextView? = null
    private var recommendationReason: TextView? = null
    private var favoriteButton: MaterialButton? = null
    private var shareButton: MaterialButton? = null

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
        setupClickListeners()
        observeViewModel()

        // Recipe ID'yi argument'tan al
        val recipeId = arguments?.getInt("recipeId", -1) ?: -1
        if (recipeId > 0) {
            viewModel.loadRecipeDetail(recipeId)
        } else {
            Toast.makeText(context, "Tarif bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews(view: View) {
        recipeImage = view.findViewById(R.id.recipeImage)
        recipeTitle = view.findViewById(R.id.recipeTitle)
        cookingTime = view.findViewById(R.id.cookingTime)
        calories = view.findViewById(R.id.calories)
        servings = view.findViewById(R.id.servings)
        recommendationReason = view.findViewById(R.id.recommendationReason)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        shareButton = view.findViewById(R.id.shareButton)
    }

    private fun setupClickListeners() {
        favoriteButton?.setOnClickListener {
            viewModel.toggleFavorite()
        }

        shareButton?.setOnClickListener {
            shareRecipe()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.recipe.collect { recipe ->
                recipe?.let { updateUI(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                updateFavoriteButton(isFavorite)
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.error?.let { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }

                state.lastAction?.let { action ->
                    val message = when (action) {
                        is DetailAction.AddedToFavorites -> "Favorilere eklendi ❤️"
                        is DetailAction.RemovedFromFavorites -> "Favorilerden çıkarıldı"
                        is DetailAction.AddedToShoppingList -> "${action.count} malzeme listeye eklendi"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    viewModel.clearLastAction()
                }
            }
        }
    }

    private fun updateUI(recipe: com.yemekonerisistemi.app.models.Recipe) {
        recipeTitle?.text = recipe.title
        cookingTime?.text = "${recipe.cookingTime} dk"
        calories?.text = "${recipe.calories} kcal"
        servings?.text = "${recipe.servings} kişilik"
        recommendationReason?.text = recipe.recommendationReason

        recipeImage?.let { imageView ->
            if (recipe.imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(recipe.imageUrl)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(imageView)
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        favoriteButton?.text = if (isFavorite) "❤️ Favorilerde" else "🤍 Favorilere Ekle"
    }

    private fun shareRecipe() {
        val shareText = viewModel.getShareText()
        if (shareText.isNotBlank()) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Tarifi Paylaş"))
        }
    }
}
