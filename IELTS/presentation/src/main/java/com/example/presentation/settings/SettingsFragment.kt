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
    
    private lateinit var readingProblemsLayout: TextInputLayout
    private lateinit var readingProblemsInput: TextInputEditText
    private lateinit var listeningProblemsLayout: TextInputLayout
    private lateinit var listeningProblemsInput: TextInputEditText
    private lateinit var speakingProblemsLayout: TextInputLayout
    private lateinit var speakingProblemsInput: TextInputEditText
    private lateinit var writingProblemsLayout: TextInputLayout
    private lateinit var writingProblemsInput: TextInputEditText
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
        readingProblemsLayout = view.findViewById(R.id.readingProblemsLayout)
        readingProblemsInput = view.findViewById(R.id.readingProblemsInput)
        listeningProblemsLayout = view.findViewById(R.id.listeningProblemsLayout)
        listeningProblemsInput = view.findViewById(R.id.listeningProblemsInput)
        speakingProblemsLayout = view.findViewById(R.id.speakingProblemsLayout)
        speakingProblemsInput = view.findViewById(R.id.speakingProblemsInput)
        writingProblemsLayout = view.findViewById(R.id.writingProblemsLayout)
        writingProblemsInput = view.findViewById(R.id.writingProblemsInput)
        studyGoalLayout = view.findViewById(R.id.studyGoalLayout)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        saveButton = view.findViewById(R.id.saveButton)
    }

    private fun loadCurrentPreferences() {
        viewModel.getUserPreferences().let { preferences ->
            readingProblemsInput.setText(preferences.readingProblems)
            listeningProblemsInput.setText(preferences.listeningProblems)
            speakingProblemsInput.setText(preferences.speakingProblems)
            writingProblemsInput.setText(preferences.writingProblems)
            
            studyGoalInput.setText(preferences.studyGoal)
        }
    }

    private fun setupButton() {
        saveButton.setOnClickListener {
            val preferences = UserPreferences(
                readingProblems = readingProblemsInput.text.toString(),
                listeningProblems = listeningProblemsInput.text.toString(),
                speakingProblems = speakingProblemsInput.text.toString(),
                writingProblems = writingProblemsInput.text.toString(),
                studyGoal = studyGoalInput.text.toString(),
                isFirstTime = false
            )
            
            viewModel.savePreferences(preferences)
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
        }
    }
} 