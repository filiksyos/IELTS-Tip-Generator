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
import androidx.appcompat.app.AlertDialog
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
    private lateinit var creditCounter: TextView
    
    private var noConnectionDialog: NoConnectionDialog? = null
    private var creditExhaustedDialog: AlertDialog? = null

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
        
        // Make sure we don't have a previously generated tip
        viewModel.clearGeneratedTip()
        
        initViews(view)
        setupListeners()
        observeViewModel()
    }
    
    private fun initViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        categoryLabel = view.findViewById(R.id.categoryLabel)
        problemInput = view.findViewById(R.id.problemInput)
        generateButton = view.findViewById(R.id.generateButton)
        exampleText = view.findViewById(R.id.exampleText)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        creditCounter = view.findViewById(R.id.creditCounter)
        
        // Set category label based on selected category
        viewModel.selectedCategory.value?.let { category ->
            categoryLabel.text = category.name
            
            // Set example text based on category
            val examplePrompt = when (category) {
                com.example.data.DashboardCategory.READING -> "e.g., I struggle with understanding academic texts quickly"
                com.example.data.DashboardCategory.LISTENING -> "e.g., I have trouble following fast conversations"
                com.example.data.DashboardCategory.WRITING -> "e.g., I need help with essay structure"
                com.example.data.DashboardCategory.SPEAKING -> "e.g., I get nervous during the speaking test"
            }
            exampleText.text = examplePrompt
        }
    }
    
    private fun setupListeners() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        generateButton.setOnClickListener {
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                showNoConnectionDialog()
                return@setOnClickListener
            }
            
            val userInput = problemInput.text.toString().trim()
            if (userInput.isBlank()) {
                Toast.makeText(requireContext(), "Please describe your issue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.setUserInput(userInput)
            viewModel.generateTip()
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            generateButton.isEnabled = !isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.networkError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                showNoConnectionDialog()
            }
        }
        
        viewModel.serverError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                Toast.makeText(
                    requireContext(),
                    "Server error. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        viewModel.creditExhausted.observe(viewLifecycleOwner) { isExhausted ->
            if (isExhausted) {
                showCreditExhaustedDialog()
            }
        }
        
        viewModel.remainingCredits.observe(viewLifecycleOwner) { credits ->
            creditCounter.text = credits.toString()
        }
        
        viewModel.generatedTip.observe(viewLifecycleOwner) { tip ->
            if (tip != null && tip.tip.isNotBlank()) {
                findNavController().navigate(R.id.action_tipInputFragment_to_tipDisplayFragment)
            }
        }
    }
    
    private fun showNoConnectionDialog() {
        // Dismiss existing dialog if any
        noConnectionDialog?.dismiss()
        
        // Create a new dialog with retry action
        noConnectionDialog = NoConnectionDialog(
            context = requireContext(),
            isServerError = false,
            onRetryClick = {
                val userInput = problemInput.text.toString().trim()
                if (userInput.isNotBlank() && NetworkUtils.isNetworkAvailable(requireContext())) {
                    viewModel.resetErrorStates()
                    viewModel.setUserInput(userInput)
                    viewModel.generateTip()
                }
            }
        )
        
        noConnectionDialog?.show()
    }
    
    private fun showCreditExhaustedDialog() {
        // Dismiss existing dialog if any
        creditExhaustedDialog?.dismiss()
        
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_credit_exhausted, null)
        
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()
            
        // Ensure dialog appears in center
        dialog.window?.setGravity(android.view.Gravity.CENTER)
        
        // Set click listener for the OK button
        dialogView.findViewById<View>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
        
        creditExhaustedDialog = dialog
        dialog.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        noConnectionDialog?.dismiss()
        creditExhaustedDialog?.dismiss()
        
        // Show bottom navigation when leaving this fragment
        (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }
}