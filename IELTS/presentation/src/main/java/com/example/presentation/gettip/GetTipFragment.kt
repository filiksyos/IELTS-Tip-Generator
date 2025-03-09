package com.example.presentation.gettip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.DashboardCategory
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GetTipFragment : Fragment() {
    
    private val viewModel: GetTipViewModel by viewModel()
    
    private lateinit var readingOption: CardView
    private lateinit var listeningOption: CardView
    private lateinit var writingOption: CardView
    private lateinit var speakingOption: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_get_tip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        readingOption = view.findViewById(R.id.readingOption)
        listeningOption = view.findViewById(R.id.listeningOption)
        writingOption = view.findViewById(R.id.writingOption)
        speakingOption = view.findViewById(R.id.speakingOption)
    }

    private fun setupClickListeners() {
        readingOption.setOnClickListener {
            navigateToInputScreen(DashboardCategory.READING)
        }
        
        listeningOption.setOnClickListener {
            navigateToInputScreen(DashboardCategory.LISTENING)
        }
        
        writingOption.setOnClickListener {
            navigateToInputScreen(DashboardCategory.WRITING)
        }
        
        speakingOption.setOnClickListener {
            navigateToInputScreen(DashboardCategory.SPEAKING)
        }
    }
    
    private fun navigateToInputScreen(category: DashboardCategory) {
        viewModel.setSelectedCategory(category)
        
        val action = when (category) {
            DashboardCategory.READING -> R.id.action_getTipFragment_to_tipInputFragment
            DashboardCategory.LISTENING -> R.id.action_getTipFragment_to_tipInputFragment
            DashboardCategory.WRITING -> R.id.action_getTipFragment_to_tipInputFragment
            DashboardCategory.SPEAKING -> R.id.action_getTipFragment_to_tipInputFragment
        }
        
        findNavController().navigate(action)
    }
} 