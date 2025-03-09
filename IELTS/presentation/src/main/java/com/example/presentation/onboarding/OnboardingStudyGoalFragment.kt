package com.example.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.models.UserPreferences
import com.example.presentation.R
import com.example.presentation.viewModel.OnboardingViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OnboardingStudyGoalFragment : Fragment() {
    
    private val viewModel: OnboardingViewModel by sharedViewModel()
    
    private lateinit var backButton: ImageButton
    private lateinit var studyGoalInput: TextInputEditText
    private lateinit var finishButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_study_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        finishButton = view.findViewById(R.id.finishButton)
        
        // Hide back button on initial onboarding
        if (viewModel.isFirstTime()) {
            backButton.visibility = View.GONE
        }
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        finishButton.setOnClickListener {
            // Save only the study goal
            val studyGoal = studyGoalInput.text.toString()
            
            val preferences = UserPreferences(
                studyGoal = studyGoal,
                isFirstTime = false
            )
            
            // Save the preferences
            viewModel.savePreferences(preferences)
            
            // Navigate to the main screen
            findNavController().navigate(R.id.action_study_goal_to_main)
        }
    }
} 