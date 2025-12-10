package com.yemekonerisistemi.app.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R
import com.yemekonerisistemi.app.models.InventoryItem

/**
 * Envanter listesi için RecyclerView Adapter
 */
class InventoryAdapter(
    private var items: MutableList<InventoryItem> = mutableListOf(),
    private val onQuantityChanged: (InventoryItem) -> Unit,
    private val onItemDeleted: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ingredientName: TextView? = itemView.findViewById(R.id.ingredientName)
        private val quantityText: TextView? = itemView.findViewById(R.id.quantityText)
        private val unitText: TextView? = itemView.findViewById(R.id.unitText)
        private val decreaseButton: ImageButton? = itemView.findViewById(R.id.decreaseButton)
        private val increaseButton: ImageButton? = itemView.findViewById(R.id.increaseButton)
        private val deleteButton: ImageButton? = itemView.findViewById(R.id.deleteButton)

        fun bind(item: InventoryItem) {
            ingredientName?.text = item.name
            quantityText?.text = item.quantity.toString()
            unitText?.text = item.unit

            decreaseButton?.setOnClickListener {
                if (item.quantity > 1) {
                    val updatedItem = item.copy(quantity = item.quantity - 1)
                    onQuantityChanged(updatedItem)
                } else {
                    onItemDeleted(item)
                }
            }

            increaseButton?.setOnClickListener {
                val updatedItem = item.copy(quantity = item.quantity + 1)
                onQuantityChanged(updatedItem)
            }

            deleteButton?.setOnClickListener {
                onItemDeleted(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<InventoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
