package com.example.recipeapp.ui.recipelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.recipeapp.viewmodel.InventoryViewModel
import com.example.recipeapp.viewmodel.RecipeListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeListFragment : Fragment() {

    private val recipeViewModel: RecipeListViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe real inventory data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                inventoryViewModel.ingredients.collect { ingredientList ->
                    val ingredientNames = ingredientList.map { it.name }
                    if (ingredientNames.isNotEmpty()) {
                        recipeViewModel.fetchRecipes(ingredientNames)
                    }
                }
            }
        }
    }
}