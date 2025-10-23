package com.yemekonerisistemi.app.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.yemekonerisistemi.app.R

class InventoryFragment : Fragment() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var addButton: MaterialButton
    private lateinit var findRecipesButton: MaterialButton

    // Geçici malzeme listesi
    private val selectedIngredients = mutableListOf<String>()

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

        // Malzeme ekleme butonu
        addButton.setOnClickListener {
            val ingredient = searchEditText.text.toString().trim()
            if (ingredient.isNotEmpty()) {
                addIngredient(ingredient)
                searchEditText.text?.clear()
            } else {
                Toast.makeText(context, "Lütfen bir malzeme girin", Toast.LENGTH_SHORT).show()
            }
        }

        // Tarif bulma butonu
        findRecipesButton.setOnClickListener {
            if (selectedIngredients.isNotEmpty()) {
                // Tarif listesi ekranına git
                findNavController().navigate(R.id.action_inventory_to_recipeList)
            } else {
                Toast.makeText(context, "En az bir malzeme ekleyin", Toast.LENGTH_SHORT).show()
            }
        }

        // Örnek malzemeler ekle (demo için)
        addSampleIngredients()
    }

    private fun addIngredient(ingredient: String) {
        selectedIngredients.add(ingredient)
        Toast.makeText(context, "$ingredient eklendi", Toast.LENGTH_SHORT).show()
        updateIngredientDisplay()
    }

    private fun updateIngredientDisplay() {
        // Bu kısım RecyclerView ile daha iyi olur ama şimdilik basit tutalım
        findRecipesButton.text = "TARİFLERİ BUL 🍳 (${selectedIngredients.size} malzeme)"
    }

    private fun addSampleIngredients() {
        // Demo için birkaç malzeme ekleyelim
        val sampleIngredients = listOf("Domates", "Tavuk", "Biber", "Soğan")
        selectedIngredients.addAll(sampleIngredients)
        updateIngredientDisplay()
    }
}