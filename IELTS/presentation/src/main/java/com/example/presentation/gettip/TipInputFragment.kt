package com.example.presentation.gettip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.presentation.MainActivity
import com.example.presentation.R
import com.example.presentation.utils.NetworkUtils
import com.example.presentation.utils.NoConnectionDialog
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
    private lateinit var loadingIndicator: ProgressBar
    
    private var noConnectionDialog: NoConnectionDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        // Hide bottom navigation when this fragment is created
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_tip_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        
        setupViews(view)
        updateCategoryUI()
        setupClickListeners()
        observeViewModel()
        
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
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
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
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            generateButton.isEnabled = !isLoading
        }
        
        viewModel.networkError.observe(viewLifecycleOwner) { hasNetworkError ->
            if (hasNetworkError) {
                showNoConnectionDialog(false)
            }
        }
        
        viewModel.serverError.observe(viewLifecycleOwner) { hasServerError ->
            if (hasServerError) {
                showNoConnectionDialog(true)
            }
        }
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
            
            // Check for internet connectivity
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                Log.e(TAG, "No internet connection")
                showNoConnectionDialog(false)
                return@setOnClickListener
            }
            
            if (input.isNotEmpty()) {
                Log.e(TAG, "User input is not empty: $input")
                // Show loading state
                loadingIndicator.visibility = View.VISIBLE
                generateButton.isEnabled = false
                
                viewModel.setUserInput(input)
                viewModel.generateTip()
                
                // Observe loading state
                viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                    if (!isLoading) {
                        loadingIndicator.visibility = View.GONE
                        generateButton.isEnabled = true
                        
                        // Only navigate if there are no errors
                        if (viewModel.networkError.value != true && viewModel.serverError.value != true) {
                            Log.e(TAG, "Navigating to tip display fragment")
                            findNavController().navigate(R.id.action_tipInputFragment_to_tipDisplayFragment)
                        }
                    }
                }
            } else {
                // Show error - input is empty
                Log.e(TAG, "User input is empty")
                problemInput.error = "Please describe your issue"
            }
        }
    }
    
    private fun showNoConnectionDialog(isServerError: Boolean) {
        noConnectionDialog?.dismiss()
        
        noConnectionDialog = NoConnectionDialog(
            context = requireContext(),
            isServerError = isServerError,
            onRetryClick = {
                val input = problemInput.text.toString().trim()
                if (input.isNotEmpty() && NetworkUtils.isNetworkAvailable(requireContext())) {
                    viewModel.resetErrorStates()
                    viewModel.setUserInput(input)
                    viewModel.generateTip()
                } else if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    // Still no internet, show the dialog again
                    showNoConnectionDialog(false)
                }
            }
        )
        
        noConnectionDialog?.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        noConnectionDialog?.dismiss()
        noConnectionDialog = null
    }
}