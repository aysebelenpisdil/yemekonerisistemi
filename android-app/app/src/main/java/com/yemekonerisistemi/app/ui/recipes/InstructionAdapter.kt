package com.yemekonerisistemi.app.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yemekonerisistemi.app.R

/**
 * Hazırlık Adımları Adapter
 */
class InstructionAdapter(
    private val instructions: List<String>
) : RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instruction_step, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) {
        val instruction = instructions[position]
        holder.bind(position + 1, instruction)
    }

    override fun getItemCount(): Int = instructions.size

    class InstructionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stepNumberText: TextView = itemView.findViewById(R.id.stepNumberText)
        private val stepDescriptionText: TextView = itemView.findViewById(R.id.stepDescriptionText)

        fun bind(stepNumber: Int, instruction: String) {
            stepNumberText.text = stepNumber.toString()
            stepDescriptionText.text = instruction
        }
    }
}
