package com.yemekonerisistemi.app.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yemekonerisistemi.app.R

/**
 * Custom expandable preference view with animated expand/collapse
 * Shows a header with selection count and expandable chip group
 */
class ExpandablePreferenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val headerLayout: LinearLayout
    private val titleText: TextView
    private val countText: TextView
    private val expandIcon: ImageView
    private val chipGroup: ChipGroup
    private val divider: View

    private var isExpanded = false
    private var onChipSelectionChanged: ((Set<String>) -> Unit)? = null

    // Currently selected items
    private val selectedItems = mutableSetOf<String>()

    init {
        orientation = VERTICAL

        // Inflate the view
        LayoutInflater.from(context).inflate(R.layout.view_expandable_preference, this, true)

        headerLayout = findViewById(R.id.headerLayout)
        titleText = findViewById(R.id.titleText)
        countText = findViewById(R.id.countText)
        expandIcon = findViewById(R.id.expandIcon)
        chipGroup = findViewById(R.id.chipGroup)
        divider = findViewById(R.id.divider)

        // Initially collapsed
        chipGroup.visibility = View.GONE

        // Header click to expand/collapse
        headerLayout.setOnClickListener {
            toggleExpanded()
        }

        // Process custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ExpandablePreferenceView)
            val title = typedArray.getString(R.styleable.ExpandablePreferenceView_prefTitle) ?: ""
            titleText.text = title
            typedArray.recycle()
        }

        updateCountText()
    }

    /**
     * Set the title of this preference section
     */
    fun setTitle(title: String) {
        titleText.text = title
    }

    /**
     * Set chips with their labels
     * @param items List of chip labels
     */
    fun setChips(items: List<String>) {
        chipGroup.removeAllViews()

        items.forEach { item ->
            val chip = Chip(context).apply {
                text = item
                isCheckable = true
                isChecked = selectedItems.contains(item)
                setChipBackgroundColorResource(R.color.chip_background_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)
                setChipStrokeColorResource(R.color.chip_stroke_selector)

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(item)
                    } else {
                        selectedItems.remove(item)
                    }
                    updateCountText()
                    onChipSelectionChanged?.invoke(selectedItems.toSet())
                }
            }
            chipGroup.addView(chip)
        }
    }

    /**
     * Set the currently selected items
     * @param items Set of selected item labels
     */
    fun setSelectedItems(items: Set<String>) {
        selectedItems.clear()
        selectedItems.addAll(items)

        // Update chip states
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.let {
                it.isChecked = selectedItems.contains(it.text.toString())
            }
        }
        updateCountText()
    }

    /**
     * Get the currently selected items
     */
    fun getSelectedItems(): Set<String> = selectedItems.toSet()

    /**
     * Set callback for when chip selection changes
     */
    fun setOnSelectionChangedListener(listener: (Set<String>) -> Unit) {
        onChipSelectionChanged = listener
    }

    /**
     * Toggle expanded/collapsed state with animation
     */
    private fun toggleExpanded() {
        isExpanded = !isExpanded
        animateExpansion(isExpanded)
    }

    /**
     * Set expanded state programmatically
     */
    fun setExpanded(expanded: Boolean, animate: Boolean = true) {
        if (isExpanded != expanded) {
            isExpanded = expanded
            if (animate) {
                animateExpansion(expanded)
            } else {
                chipGroup.visibility = if (expanded) View.VISIBLE else View.GONE
                expandIcon.rotation = if (expanded) 180f else 0f
            }
        }
    }

    /**
     * Animate the expand/collapse transition
     */
    private fun animateExpansion(expand: Boolean) {
        // Rotate expand icon
        val targetRotation = if (expand) 180f else 0f
        expandIcon.animate()
            .rotation(targetRotation)
            .setDuration(200)
            .start()

        if (expand) {
            // Expanding
            chipGroup.visibility = View.VISIBLE
            chipGroup.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val targetHeight = chipGroup.measuredHeight

            chipGroup.layoutParams.height = 0
            chipGroup.requestLayout()

            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                chipGroup.layoutParams.height = value
                chipGroup.requestLayout()
            }
            animator.duration = 200
            animator.start()
        } else {
            // Collapsing
            val initialHeight = chipGroup.height

            val animator = ValueAnimator.ofInt(initialHeight, 0)
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                chipGroup.layoutParams.height = value
                chipGroup.requestLayout()
                if (value == 0) {
                    chipGroup.visibility = View.GONE
                }
            }
            animator.duration = 200
            animator.start()
        }
    }

    /**
     * Update the count text in the header
     */
    private fun updateCountText() {
        val count = selectedItems.size
        countText.text = "($count)"
        countText.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Show or hide the bottom divider
     */
    fun setShowDivider(show: Boolean) {
        divider.visibility = if (show) View.VISIBLE else View.GONE
    }
}
