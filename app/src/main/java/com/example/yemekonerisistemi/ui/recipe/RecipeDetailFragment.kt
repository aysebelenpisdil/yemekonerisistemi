package com.example.recipeapp.ui.recipedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.recipeapp.databinding.FragmentRecipeDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {

    private val viewModel: RecipeDetailViewModel by viewModels()
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: return

        // Fetch recipe details from backend
        viewModel.fetchRecipeDetail(recipeId)

        // Observe UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RecipeDetailUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                        }
                        is RecipeDetailUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentLayout.visibility = View.VISIBLE
                            binding.errorLayout.visibility = View.GONE
                            displayRecipeDetails(state.recipe)
                        }
                        is RecipeDetailUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentLayout.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.errorText.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun displayRecipeDetails(recipe: RecipeDetail) {
        binding.recipeName.text = recipe.name
        binding.recipeDescription.text = recipe.description
        binding.recipeInstructions.text = recipe.instructions
        // ...bind other fields...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}