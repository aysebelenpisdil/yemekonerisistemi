package com.yemekonerisistemi.app.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.InventoryItem

class InventoryFragment : Fragment() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var findRecipesButton: MaterialButton
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var inventoryAdapter: InventoryAdapter

    // Malzeme listesi
    private val inventoryItems = mutableListOf<InventoryItem>()

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
        addButton = view.findViewById(R.id.addButton)
        findRecipesButton = view.findViewById(R.id.findRecipesButton)
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)

        // RecyclerView kurulumu
        setupRecyclerView()

        // Malzeme ekleme butonu
        addButton.setOnClickListener {
            val ingredientName = searchEditText.text.toString().trim()
            if (ingredientName.isNotEmpty()) {
                addIngredient(ingredientName)
                searchEditText.text?.clear()
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

        // Örnek malzemeler ekle (demo için)
        addSampleIngredients()
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

    private fun addIngredient(ingredientName: String) {
        // Aynı isimde malzeme varsa miktarını artır
        val existingItem = inventoryItems.find { it.name.equals(ingredientName, ignoreCase = true) }
        if (existingItem != null) {
            existingItem.quantity++
            inventoryAdapter.updateItem(existingItem)
            Toast.makeText(context, "$ingredientName miktarı artırıldı", Toast.LENGTH_SHORT).show()
        } else {
            // Yeni malzeme ekle
            val newItem = InventoryItem(name = ingredientName)
            inventoryAdapter.addItem(newItem)
            Toast.makeText(context, "$ingredientName eklendi", Toast.LENGTH_SHORT).show()
        }
        updateIngredientDisplay()
    }

    private fun updateIngredientDisplay() {
        val totalItems = inventoryItems.sumOf { it.quantity }
        findRecipesButton.text = "TARİFLERİ BUL 🍳 (${inventoryItems.size} çeşit, $totalItems adet)"
    }

    private fun addSampleIngredients() {
        // Demo için birkaç malzeme ekleyelim
        val sampleIngredients = listOf(
            InventoryItem(name = "Yumurta", quantity = 6),
            InventoryItem(name = "Domates", quantity = 3),
            InventoryItem(name = "Tavuk Göğsü", quantity = 2),
            InventoryItem(name = "Biber", quantity = 4),
            InventoryItem(name = "Soğan", quantity = 2)
        )
        inventoryItems.addAll(sampleIngredients)
        inventoryAdapter.notifyDataSetChanged()
        updateIngredientDisplay()
    }
}