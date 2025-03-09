package com.example.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.data.models.UserPreferences
import com.example.presentation.R
import com.example.presentation.viewModel.SettingsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()
    
    private lateinit var studyGoalLayout: TextInputLayout
    private lateinit var studyGoalInput: TextInputEditText
    private lateinit var saveButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupButton()
        loadCurrentPreferences()
    }

    private fun setupViews(view: View) {
        studyGoalLayout = view.findViewById(R.id.studyGoalLayout)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        saveButton = view.findViewById(R.id.saveButton)
    }

    private fun loadCurrentPreferences() {
        viewModel.getUserPreferences().let { preferences ->
            // Set study goal
            studyGoalInput.setText(preferences.studyGoal)
        }
    }

    private fun setupButton() {
        saveButton.setOnClickListener {
            val preferences = UserPreferences(
                studyGoal = studyGoalInput.text.toString(),
                isFirstTime = false
            )
            
            viewModel.savePreferences(preferences)
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
        }
    }
} 