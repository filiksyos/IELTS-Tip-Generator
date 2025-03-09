package com.example.presentation.gettip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.DashboardCategory
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GetTipFragment : Fragment() {
    private val TAG = "IELTS_GetTipFragment"
    
    private val viewModel: GetTipViewModel by sharedViewModel()
    
    private lateinit var readingOption: CardView
    private lateinit var listeningOption: CardView
    private lateinit var writingOption: CardView
    private lateinit var speakingOption: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_get_tip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        
        setupViews(view)
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        Log.d(TAG, "Setting up views")
        readingOption = view.findViewById(R.id.readingOption)
        listeningOption = view.findViewById(R.id.listeningOption)
        writingOption = view.findViewById(R.id.writingOption)
        speakingOption = view.findViewById(R.id.speakingOption)
    }

    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners")
        
        readingOption.setOnClickListener {
            Log.e(TAG, "Reading option clicked")
            navigateToInputScreen(DashboardCategory.READING)
        }
        
        listeningOption.setOnClickListener {
            Log.e(TAG, "Listening option clicked")
            navigateToInputScreen(DashboardCategory.LISTENING)
        }
        
        writingOption.setOnClickListener {
            Log.e(TAG, "Writing option clicked")
            navigateToInputScreen(DashboardCategory.WRITING)
        }
        
        speakingOption.setOnClickListener {
            Log.e(TAG, "Speaking option clicked")
            navigateToInputScreen(DashboardCategory.SPEAKING)
        }
    }
    
    private fun navigateToInputScreen(category: DashboardCategory) {
        Log.e(TAG, "Setting selected category: $category")
        viewModel.setSelectedCategory(category)
        
        val action = R.id.action_getTipFragment_to_tipInputFragment
        Log.e(TAG, "Navigating to tip input fragment")
        findNavController().navigate(action)
    }
} 