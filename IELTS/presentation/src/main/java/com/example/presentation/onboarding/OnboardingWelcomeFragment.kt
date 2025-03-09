package com.example.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.presentation.R
import com.google.android.material.button.MaterialButton

class OnboardingWelcomeFragment : Fragment() {
    
    private lateinit var getStartedButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        getStartedButton = view.findViewById(R.id.getStartedButton)
        
        getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_reading)
        }
    }
} 