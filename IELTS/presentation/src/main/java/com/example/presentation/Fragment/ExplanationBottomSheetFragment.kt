package com.example.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.presentation.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExplanationBottomSheetFragment : BottomSheetDialogFragment() {
    
    private var tipText: String? = null
    private var explanationText: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipText = it.getString(ARG_TIP)
            explanationText = it.getString(ARG_EXPLANATION)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_explanation, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.tipTextView).text = tipText
        view.findViewById<TextView>(R.id.explanationTextView).text = explanationText
    }
    
    companion object {
        private const val ARG_TIP = "tip"
        private const val ARG_EXPLANATION = "explanation"
        
        fun newInstance(tip: String, explanation: String): ExplanationBottomSheetFragment {
            return ExplanationBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TIP, tip)
                    putString(ARG_EXPLANATION, explanation)
                }
            }
        }
    }
} 