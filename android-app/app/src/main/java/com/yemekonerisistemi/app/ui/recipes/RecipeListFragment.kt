package com.yemekonerisistemi.app.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.Recipe
import com.yemekonerisistemi.app.ui.shared.SharedInventoryViewModel
import kotlinx.coroutines.launch

/**
 * Tarif Listesi Fragment
 * Backend'den tarif önerileri çeker ve gösterir
 */
class RecipeListFragment : Fragment() {

    private val viewModel: RecipeListViewModel by viewModels()

    // Activity-scoped SharedViewModel - InventoryFragment'tan envanter al
    private val sharedInventoryViewModel: SharedInventoryViewModel by activityViewModels()

    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var emptyStateLayout: View? = null
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'ları bağla
        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        // RecyclerView kurulumu
        setupRecyclerView()

        // ViewModel'i gözlemle
        observeViewModel()

        // SharedInventory'den gerçek malzemeleri al ve tarifleri yükle
        loadRecipesFromInventory()
    }

    private fun setupRecyclerView() {
        recipesRecyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            navigateToRecipeDetail(recipe)
        }
        recipesRecyclerView.adapter = recipeAdapter
    }

    /**
     * ViewModel'i gözlemle
     */
    private fun observeViewModel() {
        // Tarif listesi
        lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                recipeAdapter = RecipeAdapter(recipes) { recipe ->
                    navigateToRecipeDetail(recipe)
                }
                recipesRecyclerView.adapter = recipeAdapter

                if (recipes.isNotEmpty()) {
                    Toast.makeText(context, "${recipes.size} tarif bulundu!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // UI durumu
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Loading durumu
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                recipesRecyclerView.visibility = if (state.isLoading) View.GONE else View.VISIBLE

                // Empty state
                emptyStateLayout?.visibility = if (state.isEmpty) View.VISIBLE else View.GONE

                // Hata mesajı
                state.error?.let { error ->
                    view?.let { v ->
                        Snackbar.make(v, error, Snackbar.LENGTH_LONG).show()
                    }
                    viewModel.clearError()
                }
            }
        }
    }

    /**
     * SharedViewModel'den gerçek envanter verisiyle tarifleri yükle
     */
    private fun loadRecipesFromInventory() {
        lifecycleScope.launch {
            sharedInventoryViewModel.inventoryItems.collect { items ->
                val ingredientNames = items.map { it.name }
                if (ingredientNames.isNotEmpty()) {
                    // Gerçek envanter malzemelerini kullan
                    viewModel.loadRecipes(ingredientNames)

                    android.util.Log.d(
                        "RecipeListFragment",
                        "🥗 Envanter malzemeleri ile tarif aranıyor: $ingredientNames"
                    )
                } else {
                    // Envanter boşsa kullanıcıyı bilgilendir
                    viewModel.loadRecipes(emptyList())
                }
            }
        }
    }

    /**
     * Tarif detayına git - Recipe ID ile
     */
    private fun navigateToRecipeDetail(recipe: Recipe) {
        try {
            val bundle = bundleOf("recipeId" to recipe.id)
            findNavController().navigate(R.id.action_recipeList_to_recipeDetail, bundle)
        } catch (e: Exception) {
            Toast.makeText(context, "Tarif detayı açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}