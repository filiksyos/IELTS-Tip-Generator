package com.example.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data.DashboardItems
import com.example.presentation.databinding.DashboardCardviewItemsBinding

class DashboardAdapter(
    private val onItemClick: (DashboardItems) -> Unit
) : ListAdapter<DashboardItems, DashboardAdapter.ViewHolder>(DashboardDiffCallback()) {

    inner class ViewHolder(val binding: DashboardCardviewItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashboardItems) {
            binding.apply {
                tvItemName.text = item.itemText
                tvLessonOrTest.text = item.displayQuery
                cvItemsMainBackground.setCardBackgroundColor(item.color)
                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DashboardCardviewItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardItems>() {
        override fun areItemsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem.itemText == newItem.itemText
        }

        override fun areContentsTheSame(oldItem: DashboardItems, newItem: DashboardItems): Boolean {
            return oldItem == newItem
        }
    }
}
