package com.example.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.models.UserPreferences
import com.example.presentation.R
import com.example.presentation.viewModel.OnboardingViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Legacy onboarding fragment - replaced by multi-screen onboarding flow
 * Kept for backward compatibility
 */
class OnboardingFragment : Fragment() {
    private val viewModel: OnboardingViewModel by viewModel()
    
    private lateinit var studyGoalLayout: TextInputLayout
    private lateinit var studyGoalInput: TextInputEditText
    private lateinit var getStartedButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupButton()
    }

    private fun setupViews(view: View) {
        studyGoalLayout = view.findViewById(R.id.studyGoalLayout)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        getStartedButton = view.findViewById(R.id.getStartedButton)
    }

    private fun setupButton() {
        getStartedButton.setOnClickListener {
            val preferences = UserPreferences(
                studyGoal = studyGoalInput.text.toString(),
                isFirstTime = false
            )
            
            viewModel.savePreferences(preferences)
            
            // Navigate to the main navigation directly
            findNavController().navigate(R.id.main_navigation)
        }
    }
} 