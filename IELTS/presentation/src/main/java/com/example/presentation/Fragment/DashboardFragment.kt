package com.example.presentation.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.DashboardCategoryType
import com.example.presentation.adapter.DashboardAdapter
import com.example.presentation.databinding.FragmentDashboardBinding
import com.example.presentation.viewModel.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val dashboardViewModel: DashboardViewModel by viewModel()
    private lateinit var dashboardAdapter: DashboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupRefreshButton()
        observeViewModel()

        dashboardViewModel.loadDashboardItems()
    }

    private fun setupAdapter() {
        dashboardAdapter = DashboardAdapter { item -> navigateToYouTube(item.query) }
    }

    private fun setupRecyclerView() {
        binding.rvDashboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dashboardAdapter
        }
    }
    
    private fun setupRefreshButton() {
        binding.refreshButton.setOnClickListener {
            Log.d("DashboardFragment", "Refresh button clicked")
            dashboardViewModel.refreshQueries()
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.dashboardItems.observe(viewLifecycleOwner, Observer { itemsMap ->
            Log.d("DashboardFragment", "Observed items: $itemsMap")
            dashboardAdapter.submitCategorizedList(itemsMap)
        })
        
        // Observe refreshing state
        dashboardViewModel.isRefreshing.observe(viewLifecycleOwner, Observer { isRefreshing ->
            binding.refreshProgressBar.visibility = if (isRefreshing) View.VISIBLE else View.GONE
            binding.refreshButton.visibility = if (isRefreshing) View.INVISIBLE else View.VISIBLE
        })
    }

    private fun navigateToYouTube(query: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(query))
        startActivity(intent)
    }
}
