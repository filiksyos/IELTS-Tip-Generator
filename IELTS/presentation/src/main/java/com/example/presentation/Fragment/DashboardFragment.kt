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
import com.example.presentation.utils.RecyclerViewHelper
import com.example.presentation.viewModel.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val dashboardViewModel: DashboardViewModel by viewModel()

    private lateinit var readingAdapter: DashboardAdapter
    private lateinit var listeningAdapter: DashboardAdapter
    private lateinit var writingAdapter: DashboardAdapter
    private lateinit var speakingAdapter: DashboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupRecyclerViews()
        setupRefreshButton()
        observeViewModel()

        dashboardViewModel.loadDashboardItems()
    }

    private fun setupAdapters() {
        readingAdapter = DashboardAdapter { item -> navigateToYouTube(item.query) }
        listeningAdapter = DashboardAdapter { item -> navigateToYouTube(item.query) }
        writingAdapter = DashboardAdapter { item -> navigateToYouTube(item.query) }
        speakingAdapter = DashboardAdapter { item -> navigateToYouTube(item.query) }
    }

    private fun setupRecyclerViews() {
        RecyclerViewHelper.setupRecyclerView(binding.rvReading, requireContext(), readingAdapter)
        RecyclerViewHelper.setupRecyclerView(binding.rvListening, requireContext(), listeningAdapter)
        RecyclerViewHelper.setupRecyclerView(binding.rvWriting, requireContext(), writingAdapter)
        RecyclerViewHelper.setupRecyclerView(binding.rvSpeaking, requireContext(), speakingAdapter)
    }
    
    private fun setupRefreshButton() {
        binding.refreshButton.setOnClickListener {
            Log.d("DashboardFragment", "Refresh button clicked")
            dashboardViewModel.refreshQueries()
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.dashboardItems.observe(viewLifecycleOwner, Observer { itemsMap ->
            itemsMap[DashboardCategoryType.READING]?.let { readingAdapter.submitList(it) }
            Log.d("DashboardFragment", "Observed items: $itemsMap")
            itemsMap[DashboardCategoryType.LISTENING]?.let { listeningAdapter.submitList(it) }
            itemsMap[DashboardCategoryType.WRITING]?.let { writingAdapter.submitList(it) }
            itemsMap[DashboardCategoryType.SPEAKING]?.let { speakingAdapter.submitList(it) }
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
