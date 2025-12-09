package com.yemekonerisistemi.app.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.InventoryItem

class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onQuantityChanged: (InventoryItem) -> Unit,
    private val onItemDeleted: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientNameText)
        val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        val incrementButton: MaterialButton = itemView.findViewById(R.id.incrementButton)
        val decrementButton: MaterialButton = itemView.findViewById(R.id.decrementButton)
        val deleteButton: MaterialButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]

        holder.ingredientName.text = item.name
        holder.quantityText.text = item.quantity.toString()

        // Artır butonu
        holder.incrementButton.setOnClickListener {
            item.quantity++
            holder.quantityText.text = item.quantity.toString()
            onQuantityChanged(item)
        }

        // Azalt butonu
        holder.decrementButton.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.quantityText.text = item.quantity.toString()
                onQuantityChanged(item)
            }
        }

        // Sil butonu
        holder.deleteButton.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                items.removeAt(position)
                notifyItemRemoved(position)
                onItemDeleted(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: InventoryItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItem(item: InventoryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    fun updateItems(newItems: List<InventoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
