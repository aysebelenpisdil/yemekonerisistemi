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

        // Real-time fuzzy search'ü hemen aktif et
        setupRealTimeSearch()

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
     * Backend'den tüm malzeme isimlerini yükle (opsiyonel - fallback için)
     * Not: setupRealTimeSearch() zaten canlı backend araması yapıyor
     */
    private fun loadIngredientNames() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getIngredientNames()
                if (response.isSuccessful && response.body() != null) {
                    allIngredientNames = response.body()!!
                    android.util.Log.d("InventoryFragment", "✅ ${allIngredientNames.size} malzeme ismi yüklendi")
                } else {
                    android.util.Log.e("InventoryFragment", "❌ Malzeme isimleri yüklenemedi: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("InventoryFragment", "💥 Malzeme isimleri yükleme hatası: ${e.message}")
            }
        }
    }

    /**
     * Real-time fuzzy search setup (Trendyol benzeri)
     * Backend'den canlı arama yapar
     */
    private fun setupRealTimeSearch() {
        var searchJob: kotlinx.coroutines.Job? = null

        searchAutoComplete.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString()

                // Önceki arama job'ını iptal et (debouncing)
                searchJob?.cancel()

                // En az 2 karakter girilmişse ara
                if (query.length >= 2) {
                    searchJob = lifecycleScope.launch {
                        // 300ms bekle (debouncing)
                        kotlinx.coroutines.delay(300)

                        try {
                            android.util.Log.d("InventoryFragment", "🔍 Fuzzy search başlatıldı: '$query'")

                            // Backend'den fuzzy search ile ara
                            val response = RetrofitClient.apiService.searchIngredients(query, 20)

                            android.util.Log.d("InventoryFragment", "📡 API Response: ${response.code()}")

                            if (response.isSuccessful && response.body() != null) {
                                val results = response.body()!!.results
                                val ingredientNames = results.map { it.name }

                                android.util.Log.d("InventoryFragment", "✅ ${ingredientNames.size} sonuç bulundu: ${ingredientNames.take(5)}")

                                // UI thread'de adapter güncelle
                                activity?.runOnUiThread {
                                    val adapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_dropdown_item_1line,
                                        ingredientNames
                                    )
                                    searchAutoComplete.setAdapter(adapter)

                                    // Dropdown'ı göster
                                    if (ingredientNames.isNotEmpty()) {
                                        searchAutoComplete.showDropDown()
                                    }
                                }
                            } else {
                                android.util.Log.e("InventoryFragment", "❌ API hatası: ${response.code()} - ${response.message()}")
                                Toast.makeText(context, "Arama başarısız: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("InventoryFragment", "💥 Exception: ${e.message}", e)
                            e.printStackTrace()

                            // Kullanıcıya göster (debug için)
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Backend'e bağlanılamıyor: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        })

        // İlk threshold ayarı
        searchAutoComplete.threshold = 2

        android.util.Log.d("InventoryFragment", "🚀 Real-time fuzzy search aktif edildi!")
    }

    /**
     * AutoComplete setup (offline fallback)
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
