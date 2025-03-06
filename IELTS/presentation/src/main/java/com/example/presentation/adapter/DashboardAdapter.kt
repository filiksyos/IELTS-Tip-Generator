package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data.DashboardItems
import com.example.domain.DashboardCategoryType
import com.example.presentation.databinding.DashboardCardviewItemsBinding

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit
) : ListAdapter<DashboardAdapter.DashboardListItem, RecyclerView.ViewHolder>(DashboardDiffCallback()) {

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
                cardDescription.text = item.dashboardItem.displayTip
                root.setCardBackgroundColor(item.dashboardItem.color)
                root.setOnClickListener { onItemClick(item.dashboardItem) }
            }
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