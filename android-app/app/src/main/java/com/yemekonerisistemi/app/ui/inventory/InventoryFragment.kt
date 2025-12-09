package com.yemekonerisistemi.app.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.InventoryItem
import kotlinx.coroutines.launch

/**
 * Envanter (Buzdolabı) Fragment
 * Spec Kit'e göre TEMEL işlevsellik:
 * - Backend'den malzeme arama
 * - Buzdolabına ekleme
 * - Miktar yönetimi
 */
class InventoryFragment : Fragment() {

    private val viewModel: InventoryViewModel by viewModels()

    private lateinit var searchEditText: TextInputEditText
    private lateinit var suggestionsCard: MaterialCardView
    private lateinit var suggestionsRecyclerView: RecyclerView
    private lateinit var filterButton: MaterialButton
    private lateinit var addButton: MaterialButton
    private lateinit var findRecipesButton: MaterialButton
    private lateinit var categoriesRecyclerView: RecyclerView

    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var suggestionAdapter: SearchSuggestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'ları bağla
        searchEditText = view.findViewById(R.id.searchEditText)
        suggestionsCard = view.findViewById(R.id.suggestionsCard)
        suggestionsRecyclerView = view.findViewById(R.id.suggestionsRecyclerView)
        filterButton = view.findViewById(R.id.filterButton)
        addButton = view.findViewById(R.id.addButton)
        findRecipesButton = view.findViewById(R.id.findRecipesButton)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)

        // RecyclerView kurulumu
        setupInventoryRecyclerView()
        setupSuggestionsRecyclerView()

        // Real-time fuzzy search
        setupRealTimeSearch()

        // Filtre butonu
        filterButton.setOnClickListener {
            Toast.makeText(context, "Filtre özelliği yakında eklenecek!", Toast.LENGTH_SHORT).show()
        }

        // Malzeme ekleme butonu
        addButton.setOnClickListener {
            val ingredientName = searchEditText.text.toString().trim()
            if (ingredientName.isNotEmpty()) {
                viewModel.addIngredient(ingredientName)
                searchEditText.text?.clear()
            } else {
                Toast.makeText(context, "Lütfen bir malzeme girin", Toast.LENGTH_SHORT).show()
            }
        }

        // Tarif bulma butonu
        findRecipesButton.setOnClickListener {
            if (viewModel.getTotalItemCount() > 0) {
                findNavController().navigate(R.id.action_inventory_to_recipeList)
            } else {
                Toast.makeText(context, "En az bir malzeme ekleyin", Toast.LENGTH_SHORT).show()
            }
        }

        // ViewModel'i gözlemle
        observeViewModel()

        android.util.Log.d("InventoryFragment", "🚀 InventoryFragment initialized with ViewModel")
    }

    private fun setupInventoryRecyclerView() {
        inventoryAdapter = InventoryAdapter(
            items = mutableListOf(),
            onQuantityChanged = { item ->
                viewModel.updateQuantity(item, item.quantity)
            },
            onItemDeleted = { item ->
                viewModel.removeIngredient(item)
            }
        )

        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = inventoryAdapter
        }
    }

    private fun setupSuggestionsRecyclerView() {
        suggestionAdapter = SearchSuggestionAdapter(
            onSuggestionClick = { ingredientName ->
                // Öneriye tıklandığında malzeme ekle
                viewModel.addIngredient(ingredientName)
                searchEditText.text?.clear()
            }
        )

        suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionAdapter
        }
    }


    /**
     * Real-time fuzzy search setup
     * ViewModel üzerinden arama yapar
     */
    private fun setupRealTimeSearch() {
        searchEditText.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            viewModel.searchIngredients(query)
        }

        android.util.Log.d("InventoryFragment", "🚀 Real-time fuzzy search aktif!")
    }

    /**
     * ViewModel'i gözlemle
     */
    private fun observeViewModel() {
        // Envanter listesi
        lifecycleScope.launch {
            viewModel.inventoryItems.collect { items ->
                inventoryAdapter.updateItems(items)
                updateIngredientDisplay(items)
            }
        }

        // Arama önerileri
        lifecycleScope.launch {
            viewModel.searchSuggestions.collect { suggestions ->
                suggestionAdapter.updateSuggestions(suggestions)
            }
        }

        // UI durumu
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Öneriler göster/gizle
                suggestionsCard.visibility = if (state.showSuggestions) View.VISIBLE else View.GONE

                // Hata mesajı
                state.error?.let { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }

                // Aksiyon sonuçları
                state.lastAction?.let { action ->
                    val message = when (action) {
                        is ActionResult.ItemAdded -> "${action.name} eklendi"
                        is ActionResult.ItemRemoved -> "${action.name} silindi"
                        is ActionResult.QuantityIncreased -> "${action.name} miktarı artırıldı"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    viewModel.clearLastAction()
                }
            }
        }
    }

    /**
     * Buton metnini güncelle
     */
    private fun updateIngredientDisplay(items: List<InventoryItem>) {
        val totalItems = items.sumOf { it.quantity }
        findRecipesButton.text = "TARİFLERİ BUL 🍳 (${items.size} çeşit, $totalItems adet)"
    }
}
