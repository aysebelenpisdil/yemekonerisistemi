package com.yemekonerisistemi.app.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.api.RetrofitClient
import com.yemekonerisistemi.app.models.InventoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Envanter (Buzdolabı) Fragment
 * Spec Kit'e göre TEMEL işlevsellik:
 * - Backend'den malzeme arama
 * - Buzdolabına ekleme
 * - Miktar yönetimi
 */
class InventoryFragment : Fragment() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var suggestionsCard: MaterialCardView
    private lateinit var suggestionsRecyclerView: RecyclerView
    private lateinit var filterButton: MaterialButton
    private lateinit var addButton: MaterialButton
    private lateinit var findRecipesButton: MaterialButton
    private lateinit var categoriesRecyclerView: RecyclerView

    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var suggestionAdapter: SearchSuggestionAdapter

    // Malzeme listesi (buzdolabı)
    private val inventoryItems = mutableListOf<InventoryItem>()

    // Arama job'ı (debouncing için)
    private var searchJob: Job? = null

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

        // Real-time fuzzy search (Trendyol-style)
        setupRealTimeSearch()

        // Filtre butonu
        filterButton.setOnClickListener {
            Toast.makeText(context, "Filtre özelliği yakında eklenecek!", Toast.LENGTH_SHORT).show()
        }

        // Malzeme ekleme butonu
        addButton.setOnClickListener {
            val ingredientName = searchEditText.text.toString().trim()
            if (ingredientName.isNotEmpty()) {
                addIngredient(ingredientName)
                searchEditText.text?.clear()
                hideSuggestions()
            } else {
                Toast.makeText(context, "Lütfen bir malzeme girin", Toast.LENGTH_SHORT).show()
            }
        }

        // Tarif bulma butonu
        findRecipesButton.setOnClickListener {
            if (inventoryItems.isNotEmpty()) {
                findNavController().navigate(R.id.action_inventory_to_recipeList)
            } else {
                Toast.makeText(context, "En az bir malzeme ekleyin", Toast.LENGTH_SHORT).show()
            }
        }

        // Demo malzemeler ekle (ilk açılışta)
        if (inventoryItems.isEmpty()) {
            addSampleIngredients()
        }

        android.util.Log.d("InventoryFragment", "🚀 InventoryFragment initialized with RecyclerView suggestions")
    }

    private fun setupInventoryRecyclerView() {
        inventoryAdapter = InventoryAdapter(
            items = inventoryItems,
            onQuantityChanged = { _ ->
                updateIngredientDisplay()
            },
            onItemDeleted = { item ->
                Toast.makeText(context, "${item.name} silindi", Toast.LENGTH_SHORT).show()
                updateIngredientDisplay()
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
                addIngredient(ingredientName)
                searchEditText.text?.clear()
                hideSuggestions()
            }
        )

        suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = suggestionAdapter
        }
    }


    /**
     * Real-time fuzzy search setup (Trendyol benzeri)
     * Backend'den canlı arama yapar ve RecyclerView'da gösterir
     */
    private fun setupRealTimeSearch() {
        searchEditText.addTextChangedListener { editable ->
            val query = editable.toString().trim()

            // Önceki arama job'ını iptal et (debouncing)
            searchJob?.cancel()

            // Eğer query boşsa önerileri gizle
            if (query.isEmpty()) {
                hideSuggestions()
                return@addTextChangedListener
            }

            // En az 2 karakter girilmişse ara
            if (query.length >= 2) {
                searchJob = lifecycleScope.launch {
                    // 300ms bekle (debouncing)
                    delay(300)

                    try {
                        android.util.Log.d("InventoryFragment", "🔍 Fuzzy search: '$query'")

                        // Backend'den fuzzy search ile ara (IO thread'de)
                        val response = withContext(Dispatchers.IO) {
                            RetrofitClient.apiService.searchIngredients(query, 20)
                        }

                        // Burası otomatik olarak Main thread'e döner
                        android.util.Log.d("InventoryFragment", "📡 Response: ${response.code()}")

                        if (response.isSuccessful && response.body() != null) {
                            val results = response.body()!!.results
                            val ingredientNames = results.map { it.name }

                            android.util.Log.d("InventoryFragment", "✅ ${ingredientNames.size} sonuç: $ingredientNames")

                            // UI güncelle (zaten Main thread'deyiz)
                            if (ingredientNames.isNotEmpty()) {
                                showSuggestions(ingredientNames)
                            } else {
                                hideSuggestions()
                            }
                        } else {
                            android.util.Log.e("InventoryFragment", "❌ API error: ${response.code()}")
                            hideSuggestions()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("InventoryFragment", "💥 Search error: ${e.message}", e)
                        e.printStackTrace()

                        // Zaten Main thread'deyiz, direkt Toast göster
                        Toast.makeText(
                            context,
                            "Backend hatası: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        hideSuggestions()
                    }
                }
            } else {
                hideSuggestions()
            }
        }

        android.util.Log.d("InventoryFragment", "🚀 Real-time fuzzy search aktif!")
    }

    /**
     * Önerileri göster
     */
    private fun showSuggestions(suggestions: List<String>) {
        suggestionAdapter.updateSuggestions(suggestions)
        suggestionsCard.visibility = View.VISIBLE
        android.util.Log.d("InventoryFragment", "📋 Suggestions shown: ${suggestions.size} items")
    }

    /**
     * Önerileri gizle
     */
    private fun hideSuggestions() {
        suggestionsCard.visibility = View.GONE
        suggestionAdapter.clearSuggestions()
    }


    /**
     * Malzeme ekleme
     */
    private fun addIngredient(ingredientName: String) {
        // Aynı isimde malzeme varsa miktarını artır
        val existingItem = inventoryItems.find {
            it.name.equals(ingredientName, ignoreCase = true)
        }

        if (existingItem != null) {
            existingItem.quantity++
            inventoryAdapter.updateItem(existingItem)
            Toast.makeText(context, "$ingredientName miktarı artırıldı", Toast.LENGTH_SHORT).show()
        } else {
            // Yeni malzeme ekle
            val newItem = InventoryItem(
                name = ingredientName,
                quantity = 1,
                unit = "adet"
            )
            inventoryAdapter.addItem(newItem)
            Toast.makeText(context, "$ingredientName eklendi", Toast.LENGTH_SHORT).show()
        }

        updateIngredientDisplay()
    }

    /**
     * Buton metnini güncelle
     */
    private fun updateIngredientDisplay() {
        val totalItems = inventoryItems.sumOf { it.quantity }
        findRecipesButton.text = "TARİFLERİ BUL 🍳 (${inventoryItems.size} çeşit, $totalItems adet)"
    }

    /**
     * Demo malzemeler (ilk açılışta)
     */
    private fun addSampleIngredients() {
        val sampleIngredients = listOf(
            InventoryItem(name = "Yumurta", quantity = 6, unit = "adet"),
            InventoryItem(name = "Domates", quantity = 3, unit = "adet"),
            InventoryItem(name = "Tavuk Göğsü", quantity = 2, unit = "adet"),
            InventoryItem(name = "Biber", quantity = 4, unit = "adet"),
            InventoryItem(name = "Soğan", quantity = 2, unit = "adet")
        )
        inventoryItems.addAll(sampleIngredients)
        inventoryAdapter.notifyDataSetChanged()
        updateIngredientDisplay()
    }
}
