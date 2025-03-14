package com.example.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.DashboardCategory
import com.example.data.DashboardItems
import com.example.data.models.SavedTip
import com.example.presentation.R
import com.example.presentation.adapter.DashboardAdapter
import com.example.presentation.databinding.FragmentDashboardBinding
import com.example.presentation.viewModel.DashboardViewModel
import com.example.presentation.viewModel.SavedTipsViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {
    
    private val TAG = "DashboardFragment"
    private val savedTipsViewModel: SavedTipsViewModel by sharedViewModel()
    private val dashboardViewModel: DashboardViewModel by viewModel()
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
        setupTabLayout()
        setupSwipeToDelete()
        observeViewModel()

        // Set title
        binding.dashboardTitle.text = "My Library"
        
        // Disable swipe refresh functionality
        binding.swipeRefreshLayout.isEnabled = false
    }
    
    private fun setupRecyclerView() {
        adapter = DashboardAdapter(
            onItemClick = { item ->
                // Handle item click - show explanation
                showExplanationBottomSheet(item)
            },
            onDeleteClick = { tipId ->
                // Handle delete click
                savedTipsViewModel.deleteTip(tipId)
            },
            onFavoriteToggle = { tipId ->
                // Handle favorite toggle
                savedTipsViewModel.toggleFavorite(tipId)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DashboardFragment.adapter
        }
    }
    
    private fun setupSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.currentList[position]
                savedTipsViewModel.deleteTip(item.id)
            }
        }
        
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }
    
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> savedTipsViewModel.setShowingFavorites(false)
                    1 -> savedTipsViewModel.setShowingFavorites(true)
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    private fun observeViewModel() {
        // Observe all tips
        savedTipsViewModel.savedTips.observe(viewLifecycleOwner) { savedTips ->
            if (!savedTipsViewModel.showingFavorites.value!!) {
                updateUI(savedTips)
            }
        }
        
        // Observe favorite tips
        savedTipsViewModel.favoriteTips.observe(viewLifecycleOwner) { favoriteTips ->
            if (savedTipsViewModel.showingFavorites.value!!) {
                updateUI(favoriteTips)
            }
        }
        
        // Observe showing favorites flag
        savedTipsViewModel.showingFavorites.observe(viewLifecycleOwner) { showingFavorites ->
            // Update the selected tab
            binding.tabLayout.getTabAt(if (showingFavorites) 1 else 0)?.select()
            
            // Update the UI with the appropriate list
            if (showingFavorites) {
                updateUI(savedTipsViewModel.favoriteTips.value ?: emptyList())
            } else {
                updateUI(savedTipsViewModel.savedTips.value ?: emptyList())
            }
        }
        
        // Observe remaining credits - just update the counter, don't show dialog
        dashboardViewModel.remainingCredits.observe(viewLifecycleOwner) { credits ->
            binding.creditCounter.text = credits.toString()
        }
        
        // Load saved tips when fragment is created
        savedTipsViewModel.loadSavedTips()
        savedTipsViewModel.loadFavoriteTips()
    }
    
    private fun updateUI(savedTips: List<SavedTip>) {
        if (savedTips.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.emptyStateText.text = if (savedTipsViewModel.showingFavorites.value!!) {
                "No favorite tips yet. Add some tips to favorites!"
            } else {
                "No saved tips yet. Go to 'Get Tips' to create some!"
            }
        } else {
            binding.emptyStateText.visibility = View.GONE
        }
        
        // Convert SavedTip to DashboardItems for the adapter
        val dashboardItems = savedTips.map { savedTip ->
            DashboardItems(
                id = savedTip.id,
                itemText = savedTip.category.title,
                cardType = "Tip",
                color = if (savedTipsViewModel.isNewlyCreatedTip(savedTip.id)) 
                    android.graphics.Color.parseColor("#A5D6A7") else null,
                explanation = savedTip.explanation,
                displayTip = savedTip.tip,
                isFavorite = savedTip.isFavorite
            )
        }
        
        // Submit list to adapter
        adapter.submitList(dashboardItems)
        
        // If this is a new tip, scroll to it
        dashboardItems.find { it.color != null }?.let { newItem ->
            val position = dashboardItems.indexOfFirst { it.id == newItem.id }
            if (position != -1) {
                binding.recyclerView.smoothScrollToPosition(position)
                // Clear the creation tracking after we've handled it
                savedTipsViewModel.clearNewTipTracking()
            }
        }
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
