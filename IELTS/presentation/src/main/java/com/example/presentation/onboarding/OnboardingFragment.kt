package com.example.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.models.IELTSSkill
import com.example.data.models.UserPreferences
import com.example.presentation.R
import com.example.presentation.viewModel.OnboardingViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : Fragment() {
    private val viewModel: OnboardingViewModel by viewModel()
    
    private lateinit var weakestSkillLayout: TextInputLayout
    private lateinit var weakestSkillDropdown: AutoCompleteTextView
    private lateinit var targetScoreSlider: Slider
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
        setupDropdown()
        setupSlider()
        setupButton()
    }

    private fun setupViews(view: View) {
        weakestSkillLayout = view.findViewById(R.id.weakestSkillLayout)
        weakestSkillDropdown = view.findViewById(R.id.weakestSkillDropdown)
        targetScoreSlider = view.findViewById(R.id.targetScoreSlider)
        studyGoalLayout = view.findViewById(R.id.studyGoalLayout)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        getStartedButton = view.findViewById(R.id.getStartedButton)
    }

    private fun setupDropdown() {
        val skills = IELTSSkill.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            skills
        )
        weakestSkillDropdown.setAdapter(adapter)
    }

    private fun setupSlider() {
        targetScoreSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                // Handle slider value change
            }
        }
    }

    private fun setupButton() {
        getStartedButton.setOnClickListener {
            val preferences = UserPreferences(
                weakestSkill = weakestSkillDropdown.text.toString(),
                targetBandScore = targetScoreSlider.value,
                studyGoal = studyGoalInput.text.toString(),
                isFirstTime = false
            )
            
            viewModel.savePreferences(preferences)
            findNavController().navigate(R.id.action_onboardingFragment_to_main)
        }
    }
} 