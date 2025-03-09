package com.example.presentation.gettip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TipInputFragment : Fragment() {
    private val TAG = "IELTS_TipInputFragment"
    
    private val viewModel: GetTipViewModel by sharedViewModel()
    
    private lateinit var backButton: ImageButton
    private lateinit var categoryLabel: TextView
    private lateinit var problemInput: TextInputEditText
    private lateinit var generateButton: MaterialButton
    private lateinit var exampleText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_tip_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        
        setupViews(view)
        updateCategoryUI()
        setupClickListeners()
        
        // Check if a category is selected
        if (viewModel.selectedCategory.value == null) {
            Log.e(TAG, "No category selected, navigating back")
            Toast.makeText(context, "Please select a category first", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun setupViews(view: View) {
        Log.d(TAG, "Setting up views")
        backButton = view.findViewById(R.id.backButton)
        categoryLabel = view.findViewById(R.id.categoryLabel)
        problemInput = view.findViewById(R.id.problemInput)
        generateButton = view.findViewById(R.id.generateButton)
        exampleText = view.findViewById(R.id.exampleText)
    }
    
    private fun updateCategoryUI() {
        val categoryName = viewModel.getCategoryName()
        Log.d(TAG, "Updating category UI for: $categoryName")
        categoryLabel.text = categoryName
        
        // Update example text based on category
        val exampleHint = when (viewModel.selectedCategory.value) {
            com.example.data.DashboardCategory.READING -> 
                "Example: I struggle with understanding complex passages and run out of time"
            com.example.data.DashboardCategory.LISTENING -> 
                "Example: I have trouble understanding different accents and fast speech"
            com.example.data.DashboardCategory.WRITING -> 
                "Example: I find it difficult to organize my ideas and use appropriate vocabulary"
            com.example.data.DashboardCategory.SPEAKING -> 
                "Example: I get nervous during speaking tests and struggle with fluency"
            else -> ""
        }
        
        exampleText.text = exampleHint
        Log.d(TAG, "Set example hint: $exampleHint")
    }

    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners")
        
        backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            findNavController().navigateUp()
        }
        
        generateButton.setOnClickListener {
            Log.e(TAG, "Generate button clicked")
            val input = problemInput.text.toString().trim()
            
            // Check if a category is selected
            if (viewModel.selectedCategory.value == null) {
                Log.e(TAG, "Cannot generate tip: No category selected")
                Toast.makeText(context, "Please select a category first", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
                return@setOnClickListener
            }
            
            if (input.isNotEmpty()) {
                Log.e(TAG, "User input is not empty: $input")
                viewModel.setUserInput(input)
                viewModel.generateTip()
                Log.e(TAG, "Navigating to tip display fragment")
                findNavController().navigate(R.id.action_tipInputFragment_to_tipDisplayFragment)
            } else {
                // Show error - input is empty
                Log.e(TAG, "User input is empty")
                problemInput.error = "Please describe your issue"
            }
        }
    }
} 