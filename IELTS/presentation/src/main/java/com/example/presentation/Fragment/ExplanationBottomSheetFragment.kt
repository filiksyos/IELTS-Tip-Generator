package com.example.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.presentation.databinding.BottomSheetExplanationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExplanationBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetExplanationBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetExplanationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            binding.tvTipTitle.text = args.getString(ARG_TIP)
            binding.tvExplanation.text = args.getString(ARG_EXPLANATION)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 