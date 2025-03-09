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
        
        backButton = view.findViewById(R.id.backButton)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        finishButton = view.findViewById(R.id.finishButton)
        
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_study_goal_to_writing)
        }
        
        finishButton.setOnClickListener {
            // Save the study goal to the ViewModel
            viewModel.setStudyGoal(studyGoalInput.text.toString())
            
            // Create and save the final UserPreferences object
            val preferences = UserPreferences(
                readingProblems = viewModel.getReadingProblems(),
                listeningProblems = viewModel.getListeningProblems(),
                speakingProblems = viewModel.getSpeakingProblems(),
                writingProblems = viewModel.getWritingProblems(),
                studyGoal = viewModel.getStudyGoal(),
                isFirstTime = false
            )
            
            // Save the preferences
            viewModel.savePreferences(preferences)
            
            // Navigate to the main screen
            findNavController().navigate(R.id.action_study_goal_to_main)
        }
    }
} 