package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data.DashboardItems
import com.example.presentation.R
import com.example.presentation.databinding.DashboardCardviewItemsBinding
import android.animation.ValueAnimator
import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val onFavoriteToggle: (String) -> Unit
) : ListAdapter<DashboardItems, DashboardAdapter.ItemViewHolder>(DashboardDiffCallback()) {

    private val TAG = "DashboardAdapter"
    // Track which items have been animated
    private val animatedItems = mutableSetOf<String>()
    // Track active animations
    private val activeAnimations = mutableMapOf<String, ValueAnimator>()
    // Shared recycled view pool for better performance
    private val recycledViewPool = RecycledViewPool()
    
    // Animation constants
    private val ANIMATION_DURATION = 1500L // 1.5 seconds (reduced from 5s)
    private val START_COLOR = Color.parseColor("#63c267") // Light green
    private val END_COLOR = Color.WHITE

    inner class ItemViewHolder(private val binding: DashboardCardviewItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        
        @SuppressLint("ResourceType")
        fun bind(item: DashboardItems) {
            with(binding) {
                // Set basic item data
                cardTitle.text = item.itemText
                cardDescription.text = item.displayTip
                
                // Set favorite icon state
                favoriteIcon.setImageResource(
                    if (item.isFavorite) 
                        R.xml.ic_favorite
                    else 
                        R.xml.ic_favorite_border
                )
                
                // Handle background color animation for new items
                if (item.color != null && !animatedItems.contains(item.id)) {
                    // Cancel any existing animation for this item
                    activeAnimations[item.id]?.cancel()
                    
                    // Create and start new animation
                    val animator = ValueAnimator.ofObject(
                        ArgbEvaluator(),
                        START_COLOR,
                        END_COLOR
                    ).apply {
                        duration = ANIMATION_DURATION
                        addUpdateListener { animation ->
                            root.setCardBackgroundColor(animation.animatedValue as Int)
                        }
                        doOnEnd {
                            // Remove from active animations when complete
                            activeAnimations.remove(item.id)
                            // Mark as animated
                            animatedItems.add(item.id)
                        }
                        start()
                    }
                    
                    // Store the animation
                    activeAnimations[item.id] = animator
                } else {
                    // Set default background for non-animated items
                    root.setCardBackgroundColor(Color.WHITE)
                }
                
                // Set click listeners
                root.setOnClickListener {
                    onItemClick(item)
                }
                
                // Note: There's no deleteIcon in the layout, so we'll use the favoriteIcon for toggling favorites
                favoriteIcon.setOnClickListener {
                    onFavoriteToggle(item.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DashboardCardviewItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
    
    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        // Clean up any resources when view is recycled
    }
    
    override fun onFailedToRecycleView(holder: ItemViewHolder): Boolean {
        // Ensure view can be recycled even if it has animations
        return true
    }
    
    fun clearAnimations() {
        // Cancel all active animations
        activeAnimations.values.forEach { it.cancel() }
        activeAnimations.clear()
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardItems>() {
        override fun areItemsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem.displayTip == newItem.displayTip && 
                   oldItem.explanation == newItem.explanation &&
                   oldItem.isFavorite == newItem.isFavorite
        }
    }
}
