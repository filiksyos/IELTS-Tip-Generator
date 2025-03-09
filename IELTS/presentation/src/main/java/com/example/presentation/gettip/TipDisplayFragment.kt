package com.example.presentation.gettip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.models.IELTSContent
import com.example.data.models.SavedTip
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import com.example.presentation.viewModel.SavedTipsViewModel
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TipDisplayFragment : Fragment() {
    private val TAG = "IELTS_TipDisplayFrag"
    
    private val getTipViewModel: GetTipViewModel by sharedViewModel()
    private val savedTipsViewModel: SavedTipsViewModel by sharedViewModel()
    
    private lateinit var categoryTitle: TextView
    private lateinit var tipText: TextView
    private lateinit var explanationText: TextView
    private lateinit var acceptButton: MaterialButton
    private lateinit var discardButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_tip_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        
        setupViews(view)
        
        // Check if a category is selected
        if (getTipViewModel.selectedCategory.value == null) {
            Log.e(TAG, "No category selected, navigating back")
            Toast.makeText(context, "Please select a category first", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.navigation_get_tip, false)
            return
        }
        
        observeViewModel()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        Log.d(TAG, "Setting up views")
        categoryTitle = view.findViewById(R.id.categoryTitle)
        tipText = view.findViewById(R.id.tipText)
        explanationText = view.findViewById(R.id.explanationText)
        acceptButton = view.findViewById(R.id.acceptButton)
        discardButton = view.findViewById(R.id.discardButton)
        
        // Set category title
        val categoryName = getTipViewModel.getCategoryName()
        Log.d(TAG, "Setting category title: $categoryName")
        categoryTitle.text = categoryName
    }
    
    private fun observeViewModel() {
        Log.d(TAG, "Setting up view model observers")
        
        getTipViewModel.generatedTip.observe(viewLifecycleOwner) { content ->
            Log.e(TAG, "Received generated tip: $content")
            updateTipContent(content)
        }
        
        getTipViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(TAG, "Loading state changed: $isLoading")
            acceptButton.isEnabled = !isLoading
            discardButton.isEnabled = !isLoading
        }
        
        getTipViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Log.e(TAG, "Error received: $errorMessage")
                // Show error message
                tipText.text = "Error generating tip"
                explanationText.text = errorMessage
            }
        }
    }
    
    private fun updateTipContent(content: IELTSContent) {
        Log.d(TAG, "Updating tip content: $content")
        tipText.text = content.tip
        explanationText.text = content.explanation
    }

    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners")
        
        acceptButton.setOnClickListener {
            Log.e(TAG, "Accept button clicked")
            // Save the tip
            val category = getTipViewModel.selectedCategory.value
            val tip = tipText.text.toString()
            val explanation = explanationText.text.toString()
            
            if (category != null && tip.isNotEmpty() && explanation.isNotEmpty()) {
                Log.e(TAG, "Saving tip for category: $category")
                val savedTip = SavedTip(
                    category = category,
                    tip = tip,
                    explanation = explanation
                )
                
                savedTipsViewModel.saveTip(savedTip)
                Toast.makeText(context, "Tip saved successfully", Toast.LENGTH_SHORT).show()
                
                // Navigate to the dashboard to see saved tips
                Log.e(TAG, "Navigating to dashboard")
                findNavController().navigate(R.id.navigation_dashboard)
            } else {
                Log.e(TAG, "Cannot save tip: category=$category, tip=$tip, explanation=$explanation")
                if (category == null) {
                    Toast.makeText(context, "Error: No category selected", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: Tip or explanation is empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        discardButton.setOnClickListener {
            Log.e(TAG, "Discard button clicked")
            // Discard the tip and navigate back
            Toast.makeText(context, "Tip discarded", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.navigation_get_tip, false)
        }
    }
} 