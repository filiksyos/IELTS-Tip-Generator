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
import android.graphics.Color
import android.util.Log

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val onFavoriteToggle: (String) -> Unit
) : ListAdapter<DashboardItems, DashboardAdapter.ItemViewHolder>(DashboardDiffCallback()) {

    private val TAG = "DashboardAdapter"
    // Track which items have been animated
    private val animatedItems = mutableSetOf<String>()
    // Track active animations with their start times
    private val activeAnimations = mutableMapOf<String, Pair<ValueAnimator, Long>>()
    
    // Animation constants
    private val ANIMATION_DURATION = 5000L // 5 seconds
    private val START_COLOR = Color.parseColor("#63c267") // Light green
    private val END_COLOR = Color.WHITE

    inner class ItemViewHolder(val binding: DashboardCardviewItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        private fun cancelExistingAnimation(itemId: String) {
            activeAnimations[itemId]?.first?.let { animator ->
                animator.removeAllListeners()
                animator.cancel()
                activeAnimations.remove(itemId)
            }
        }
        
        fun bind(item: DashboardItems) {
            binding.apply {
                cardTitle.text = item.itemText
                cardDescription.text = item.displayTip
                
                // Set favorite icon based on item's favorite status
                favoriteIcon.setImageResource(
                    if (item.isFavorite) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_border
                )
                
                // Handle background color and animation
                if (item.color != null && !animatedItems.contains(item.id)) {
                    // Check if there's an active animation
                    val existingAnimation = activeAnimations[item.id]
                    if (existingAnimation != null) {
                        // Animation exists, calculate remaining duration
                        val (animator, startTime) = existingAnimation
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = ANIMATION_DURATION - elapsedTime
                        
                        if (remainingTime > 0) {
                            // Continue existing animation
                            animator.currentPlayTime = elapsedTime
                        } else {
                            // Animation should be complete
                            root.setCardBackgroundColor(END_COLOR)
                            animatedItems.add(item.id)
                            activeAnimations.remove(item.id)
                        }
                    } else {
                        Log.d(TAG, "Starting new animation for item ${item.id}")
                        // Start with green color
                        root.setCardBackgroundColor(START_COLOR)
                        
                        // Create color animation
                        val colorAnimation = ValueAnimator.ofObject(
                            ArgbEvaluator(),
                            START_COLOR,
                            END_COLOR
                        ).apply {
                            duration = ANIMATION_DURATION
                            addUpdateListener { animator ->
                                if (root != null) {
                                    val color = animator.animatedValue as Int
                                    root.setCardBackgroundColor(color)
                                }
                            }
                            addListener(object : android.animation.AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: android.animation.Animator) {
                                    animatedItems.add(item.id)
                                    activeAnimations.remove(item.id)
                                    root?.setCardBackgroundColor(END_COLOR)
                                }
                            })
                        }
                        
                        // Store animation with start time
                        activeAnimations[item.id] = Pair(colorAnimation, System.currentTimeMillis())
                        colorAnimation.start()
                    }
                } else {
                    // If already animated or no color specified, set to white
                    root.setCardBackgroundColor(END_COLOR)
                }
                
                // Set click listener for the card
                root.setOnClickListener { onItemClick(item) }
                
                // Set click listener for the favorite icon
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
        // Don't cancel animations on recycle, let them continue
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardItems>() {
        override fun areItemsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem == newItem && oldItem.color == newItem.color
        }
    }
}
