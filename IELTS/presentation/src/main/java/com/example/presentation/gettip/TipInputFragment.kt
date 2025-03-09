package com.example.presentation.gettip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TipInputFragment : Fragment() {
    
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
        return inflater.inflate(R.layout.fragment_tip_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        updateCategoryUI()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        categoryLabel = view.findViewById(R.id.categoryLabel)
        problemInput = view.findViewById(R.id.problemInput)
        generateButton = view.findViewById(R.id.generateButton)
        exampleText = view.findViewById(R.id.exampleText)
    }
    
    private fun updateCategoryUI() {
        val categoryName = viewModel.getCategoryName()
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
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        generateButton.setOnClickListener {
            val input = problemInput.text.toString().trim()
            if (input.isNotEmpty()) {
                viewModel.setUserInput(input)
                viewModel.generateTip()
                findNavController().navigate(R.id.action_tipInputFragment_to_generatedTipFragment)
            } else {
                // Show error - input is empty
                problemInput.error = "Please describe your issue"
            }
        }
    }
} 