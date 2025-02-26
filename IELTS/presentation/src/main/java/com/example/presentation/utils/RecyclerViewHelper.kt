package com.example.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.adapter.DashboardAdapter

object RecyclerViewHelper {

    @SuppressLint("WrongConstant")
    fun setupRecyclerView(
        recyclerView: RecyclerView,
        context: Context,
        adapter: DashboardAdapter,
        orientation: Int = LinearLayoutManager.HORIZONTAL
    ) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, orientation, false)
            this.adapter = adapter
        }
    }
}
