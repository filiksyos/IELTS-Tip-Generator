package com.example.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.models.SavedTip
import com.example.presentation.R
import com.example.presentation.adapter.DashboardAdapter
import com.example.presentation.databinding.FragmentDashboardBinding
import com.example.presentation.viewModel.SavedTipsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {
    
    private val savedTipsViewModel: SavedTipsViewModel by viewModel()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DashboardAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        
        // Set title
        binding.dashboardTitle.text = "Your Saved Tips"
        
        // Disable swipe refresh functionality
        binding.swipeRefreshLayout.isEnabled = false
    }
    
    private fun setupRecyclerView() {
        adapter = DashboardAdapter { item ->
            // Handle item click - show explanation
            showExplanationBottomSheet(item)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DashboardFragment.adapter
        }
    }
    
    private fun observeViewModel() {
        savedTipsViewModel.savedTips.observe(viewLifecycleOwner) { savedTips ->
            updateUI(savedTips)
        }
        
        // Load saved tips when fragment is created
        savedTipsViewModel.loadSavedTips()
    }
    
    private fun updateUI(savedTips: List<SavedTip>) {
        if (savedTips.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emptyStateText.text = "No saved tips yet. Go to 'Get Tips' to create some!"
        } else {
            binding.emptyStateText.visibility = View.GONE
        }
        
        // Convert SavedTip to DashboardItems for the adapter
        val dashboardItems = savedTips.map { savedTip ->
            DashboardItems(
                itemText = savedTip.category.title,
                cardType = "Tip",
                color = savedTip.category.color,
                explanation = savedTip.explanation,
                displayTip = savedTip.tip,
                id = savedTip.id
            )
        }
        
        // Submit list to adapter
        adapter.submitList(dashboardItems)
    }
    
    private fun showExplanationBottomSheet(item: DashboardItems) {
        val bottomSheetFragment = ExplanationBottomSheetFragment.newInstance(
            item.displayTip,
            item.explanation
        )
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
