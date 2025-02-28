package com.example.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.data.models.IELTSSkill
import com.example.data.models.UserPreferences
import com.example.presentation.R
import com.example.presentation.viewModel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()
    
    private lateinit var weakestSkillSpinner: Spinner
    private lateinit var targetScoreSeekBar: SeekBar
    private lateinit var targetScoreValue: TextView
    private lateinit var studyGoalInput: EditText
    private lateinit var saveButton: Button

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
        setupSpinner()
        setupSeekBar()
        setupButton()
        loadCurrentPreferences()
    }

    private fun setupViews(view: View) {
        weakestSkillSpinner = view.findViewById(R.id.weakestSkillSpinner)
        targetScoreSeekBar = view.findViewById(R.id.targetScoreSeekBar)
        targetScoreValue = view.findViewById(R.id.targetScoreValue)
        studyGoalInput = view.findViewById(R.id.studyGoalInput)
        saveButton = view.findViewById(R.id.saveButton)
    }

    private fun setupSpinner() {
        val skills = IELTSSkill.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            skills
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        weakestSkillSpinner.adapter = adapter
    }

    private fun setupSeekBar() {
        targetScoreSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val score = (progress / 2f) + 4 // Convert to IELTS score range (4.0-9.0)
                targetScoreValue.text = String.format("%.1f", score)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun loadCurrentPreferences() {
        viewModel.getUserPreferences().let { preferences ->
            // Set spinner selection
            val skillPosition = IELTSSkill.values()
                .map { it.name }
                .indexOf(preferences.weakestSkill)
            if (skillPosition != -1) {
                weakestSkillSpinner.setSelection(skillPosition)
            }

            // Set seekbar progress
            val progress = ((preferences.targetBandScore - 4) * 2).toInt()
            targetScoreSeekBar.progress = progress
            targetScoreValue.text = String.format("%.1f", preferences.targetBandScore)

            // Set study goal
            studyGoalInput.setText(preferences.studyGoal)
        }
    }

    private fun setupButton() {
        saveButton.setOnClickListener {
            val preferences = UserPreferences(
                weakestSkill = weakestSkillSpinner.selectedItem.toString(),
                targetBandScore = targetScoreValue.text.toString().toFloat(),
                studyGoal = studyGoalInput.text.toString(),
                isFirstTime = false
            )
            
            viewModel.savePreferences(preferences)
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
        }
    }
} 