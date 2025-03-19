package com.example.presentation.gettip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.data.models.IELTSContent
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GeneratedTipFragment : Fragment() {
    
    private val viewModel: GetTipViewModel by sharedViewModel()
    
    private lateinit var categoryTitle: TextView
    private lateinit var tipText: TextView
    private lateinit var explanationText: TextView
    private lateinit var refreshButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generated_tip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        observeViewModel()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        categoryTitle = view.findViewById(R.id.categoryTitle)
        tipText = view.findViewById(R.id.tipText)
        explanationText = view.findViewById(R.id.explanationText)
        refreshButton = view.findViewById(R.id.refreshButton)
        
        // Set category title
        categoryTitle.text = viewModel.getCategoryName()
    }
    
    private fun observeViewModel() {
        viewModel.generatedTip.observe(viewLifecycleOwner) { content ->
            updateTipContent(content)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            refreshButton.isEnabled = !isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                // Show error message
                tipText.text = "Error generating tip"
                explanationText.text = errorMessage
            }
        }
    }
    
    private fun updateTipContent(content: IELTSContent) {
        tipText.text = content.tip
        explanationText.text = content.explanation
    }

    private fun setupClickListeners() {
        refreshButton.setOnClickListener {
            // Generate a new tip
            viewModel.generateTip()
        }
    }
} 