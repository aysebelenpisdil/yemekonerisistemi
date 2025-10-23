package com.yemekonerisistemi.app.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.api.RetrofitClient
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

    private lateinit var searchAutoComplete: AutoCompleteTextView
    private lateinit var filterButton: MaterialButton
    private lateinit var addButton: MaterialButton
    private lateinit var findRecipesButton: MaterialButton
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var inventoryAdapter: InventoryAdapter

    // Malzeme listesi (buzdolabı)
    private val inventoryItems = mutableListOf<InventoryItem>()

    // Backend'den gelen tüm malzeme isimleri
    private var allIngredientNames = listOf<String>()

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
        searchAutoComplete = view.findViewById(R.id.searchAutoComplete)
        filterButton = view.findViewById(R.id.filterButton)
        addButton = view.findViewById(R.id.addButton)
        findRecipesButton = view.findViewById(R.id.findRecipesButton)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)

        // RecyclerView kurulumu
        setupRecyclerView()

        // Backend'den malzeme isimlerini yükle
        loadIngredientNames()

        // Filtre butonu
        filterButton.setOnClickListener {
            // TODO: Filtre bottom sheet göster (FAZ 3'te implement edilecek)
            Toast.makeText(context, "Filtre özelliği yakında eklenecek!", Toast.LENGTH_SHORT).show()
        }

        // Malzeme ekleme butonu
        addButton.setOnClickListener {
            val ingredientName = searchAutoComplete.text.toString().trim()
            if (ingredientName.isNotEmpty()) {
                addIngredient(ingredientName)
                searchAutoComplete.text.clear()
            } else {
                Toast.makeText(context, "Lütfen bir malzeme girin", Toast.LENGTH_SHORT).show()
            }
        }

        // Tarif bulma butonu
        findRecipesButton.setOnClickListener {
            if (inventoryItems.isNotEmpty()) {
                // Tarif listesi ekranına git
                findNavController().navigate(R.id.action_inventory_to_recipeList)
            } else {
                Toast.makeText(context, "En az bir malzeme ekleyin", Toast.LENGTH_SHORT).show()
            }
        }

        // Demo malzemeler ekle (ilk açılışta)
        if (inventoryItems.isEmpty()) {
            addSampleIngredients()
        }
    }

    private fun setupRecyclerView() {
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

    /**
     * Backend'den tüm malzeme isimlerini yükle (autocomplete için)
     */
    private fun loadIngredientNames() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getIngredientNames()
                if (response.isSuccessful && response.body() != null) {
                    allIngredientNames = response.body()!!
                    setupAutoComplete(allIngredientNames)
                } else {
                    Toast.makeText(
                        context,
                        "Malzemeler yüklenemedi: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Backend'e bağlanılamıyor. Backend'in çalıştığından emin olun.",
                    Toast.LENGTH_LONG
                ).show()
                // Offline fallback - demo isimleri
                setupAutoCompleteFallback()
            }
        }
    }

    /**
     * AutoComplete setup
     */
    private fun setupAutoComplete(ingredientNames: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ingredientNames
        )
        searchAutoComplete.setAdapter(adapter)
        searchAutoComplete.threshold = 2 // 2 karakterden sonra öner
    }

    /**
     * Offline fallback - Backend çalışmıyorsa demo isimler
     */
    private fun setupAutoCompleteFallback() {
        val demoNames = listOf(
            "Domates", "Biber", "Soğan", "Tavuk", "Et", "Yumurta",
            "Süt", "Peynir", "Makarna", "Pirinç", "Mercimek",
            "Patates", "Havuç", "Salatalık", "Marul"
        )
        setupAutoComplete(demoNames)
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
