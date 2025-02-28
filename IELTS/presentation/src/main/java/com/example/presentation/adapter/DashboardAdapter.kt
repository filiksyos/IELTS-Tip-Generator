package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data.DashboardItems
import com.example.domain.DashboardCategoryType
import com.example.presentation.databinding.DashboardCardviewItemsBinding
import com.example.presentation.databinding.DashboardSectionHeaderBinding

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit
) : ListAdapter<DashboardAdapter.DashboardListItem, RecyclerView.ViewHolder>(DashboardDiffCallback()) {

    sealed class DashboardListItem {
        data class Header(val title: String) : DashboardListItem()
        data class Item(val dashboardItem: DashboardItems) : DashboardListItem()
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    class HeaderViewHolder(val binding: DashboardSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: DashboardListItem.Header) {
            binding.tvSectionTitle.text = header.title
        }
    }

    inner class ItemViewHolder(val binding: DashboardCardviewItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashboardListItem.Item) {
            binding.apply {
                tvItemName.text = item.dashboardItem.itemText
                tvLessonOrTest.text = item.dashboardItem.displayQuery
                cvItemsMainBackground.setCardBackgroundColor(item.dashboardItem.color)
                root.setOnClickListener { onItemClick(item.dashboardItem) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DashboardListItem.Header -> VIEW_TYPE_HEADER
            is DashboardListItem.Item -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = DashboardSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = DashboardCardviewItemsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DashboardListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is DashboardListItem.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardListItem>() {
        override fun areItemsTheSame(oldItem: DashboardListItem, newItem: DashboardListItem): Boolean {
            return when {
                oldItem is DashboardListItem.Header && newItem is DashboardListItem.Header ->
                    oldItem.title == newItem.title
                oldItem is DashboardListItem.Item && newItem is DashboardListItem.Item ->
                    oldItem.dashboardItem.itemText == newItem.dashboardItem.itemText
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: DashboardListItem, newItem: DashboardListItem): Boolean {
            return oldItem == newItem
        }
    }

    fun submitCategorizedList(itemsMap: Map<DashboardCategoryType, List<DashboardItems>>) {
        val consolidatedList = mutableListOf<DashboardListItem>()
        
        // Add Reading section
        itemsMap[DashboardCategoryType.READING]?.let { items ->
            if (items.isNotEmpty()) {
                consolidatedList.add(DashboardListItem.Header("Reading"))
                items.forEach { consolidatedList.add(DashboardListItem.Item(it)) }
            }
        }

        // Add Listening section
        itemsMap[DashboardCategoryType.LISTENING]?.let { items ->
            if (items.isNotEmpty()) {
                consolidatedList.add(DashboardListItem.Header("Listening"))
                items.forEach { consolidatedList.add(DashboardListItem.Item(it)) }
            }
        }

        // Add Writing section
        itemsMap[DashboardCategoryType.WRITING]?.let { items ->
            if (items.isNotEmpty()) {
                consolidatedList.add(DashboardListItem.Header("Writing"))
                items.forEach { consolidatedList.add(DashboardListItem.Item(it)) }
            }
        }

        // Add Speaking section
        itemsMap[DashboardCategoryType.SPEAKING]?.let { items ->
            if (items.isNotEmpty()) {
                consolidatedList.add(DashboardListItem.Header("Speaking"))
                items.forEach { consolidatedList.add(DashboardListItem.Item(it)) }
            }
        }

        submitList(consolidatedList)
    }
}
