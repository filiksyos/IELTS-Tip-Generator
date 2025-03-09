package com.example.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.presentation.R
import com.example.presentation.viewModel.OnboardingViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OnboardingListeningFragment : Fragment() {
    
    private val viewModel: OnboardingViewModel by sharedViewModel()
    
    private lateinit var backButton: ImageButton
    private lateinit var listeningProblemsInput: TextInputEditText
    private lateinit var continueButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_listening, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        backButton = view.findViewById(R.id.backButton)
        listeningProblemsInput = view.findViewById(R.id.listeningProblemsInput)
        continueButton = view.findViewById(R.id.continueButton)
        
        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_listening_to_reading)
        }
        
        continueButton.setOnClickListener {
            // Save the listening problems to the ViewModel
            viewModel.setListeningProblems(listeningProblemsInput.text.toString())
            
            // Navigate to the next screen
            findNavController().navigate(R.id.action_listening_to_speaking)
        }
    }
} 