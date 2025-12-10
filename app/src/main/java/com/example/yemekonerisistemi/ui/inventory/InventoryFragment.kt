package com.example.inventoryapp.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.inventoryapp.R
import com.example.inventoryapp.data.Ingredient
import com.example.inventoryapp.databinding.FragmentInventoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private val viewModel: InventoryViewModel by activityViewModels()
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Load inventory from ViewModel (will persist across config changes)
        viewModel.loadInventory()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter { ingredient -> onDeleteItem(ingredient) }
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ingredients.collect { items ->
                    adapter.submitList(items)
                    binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            showAddIngredientDialog()
        }
    }

    private fun showAddIngredientDialog() {
        // Show dialog and delegate add operation to ViewModel
        // viewModel.addIngredient(ingredient)
    }

    private fun onDeleteItem(ingredient: Ingredient) {
        // Delegate delete operation to ViewModel
        viewModel.removeIngredient(ingredient)
    }

    // TODO: Implement Room database for persistent storage
    // Currently data is held in ViewModel and survives config changes
    // but is lost on process death. Room implementation needed for full persistence.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}