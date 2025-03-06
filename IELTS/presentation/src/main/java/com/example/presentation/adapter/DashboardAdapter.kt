package com.example.presentation.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.DashboardItems
import com.example.domain.DashboardCategoryType
import com.example.presentation.R
import com.example.presentation.databinding.DashboardCardviewItemsBinding

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit
) : ListAdapter<DashboardAdapter.DashboardListItem, RecyclerView.ViewHolder>(DashboardDiffCallback()) {

    private val TAG = "DashboardAdapter"

    sealed class DashboardListItem {
        data class Item(val dashboardItem: DashboardItems) : DashboardListItem()
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 1
    }

    inner class ItemViewHolder(val binding: DashboardCardviewItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashboardListItem.Item) {
            binding.apply {
                cardTitle.text = item.dashboardItem.itemText
                cardDescription.text = item.dashboardItem.displayQuery
                root.setCardBackgroundColor(item.dashboardItem.color)
                root.setOnClickListener { onItemClick(item.dashboardItem) }
                
                // Load image using Glide
                item.dashboardItem.itemImageUri?.let { uriString ->
                    Log.d(TAG, "Attempting to load image from URI: $uriString")
                    
                    try {
                        // Parse the URI and extract resource ID
                        val uri = Uri.parse(uriString)
                        if (uri.scheme == "android.resource") {
                            val resourceName = uri.lastPathSegment
                            Log.d(TAG, "Resource name extracted: $resourceName")
                            
                            // Get resource ID directly
                            val resId = getResourceId(binding.root.context, resourceName ?: "")
                            Log.d(TAG, "Resource ID resolved: $resId")
                            
                            if (resId != 0) {
                                Glide.with(binding.root.context)
                                    .load(resId)
                                    .centerInside()
                                    .error(R.drawable.placeholder)
                                    .into(imageViewItemImage)
                            } else {
                                Log.e(TAG, "Could not resolve resource ID for: $resourceName")
                                imageViewItemImage.setImageResource(R.drawable.placeholder)
                            }
                        } else {
                            Log.e(TAG, "Invalid URI scheme: ${uri.scheme}, expected android.resource")
                            imageViewItemImage.setImageResource(R.drawable.placeholder)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading image: $uriString", e)
                        imageViewItemImage.setImageResource(R.drawable.placeholder)
                    }
                } ?: run {
                    Log.w(TAG, "No image URI for item: ${item.dashboardItem.itemText}")
                    imageViewItemImage.setImageResource(R.drawable.placeholder)
                }
            }
        }
        
        private fun getResourceId(context: Context, resourceName: String): Int {
            val packageName = context.packageName
            Log.d(TAG, "Looking up resource '$resourceName' in package: $packageName")
            return context.resources.getIdentifier(resourceName, "drawable", packageName)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DashboardCardviewItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(getItem(position) as DashboardListItem.Item)
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardListItem>() {
        override fun areItemsTheSame(oldItem: DashboardListItem, newItem: DashboardListItem): Boolean {
            return (oldItem as DashboardListItem.Item).dashboardItem.itemText == (newItem as DashboardListItem.Item).dashboardItem.itemText
        }

        override fun areContentsTheSame(oldItem: DashboardListItem, newItem: DashboardListItem): Boolean {
            return oldItem == newItem
        }
    }

    fun submitCategorizedList(itemsMap: Map<DashboardCategoryType, List<DashboardItems>>) {
        val consolidatedList = mutableListOf<DashboardListItem>()
        itemsMap.values.flatten().forEach { item ->
            consolidatedList.add(DashboardListItem.Item(item))
        }
        submitList(consolidatedList)
    }
}