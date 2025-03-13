package com.example.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.R
import com.google.android.material.card.MaterialCardView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

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
        setupRecyclerView()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.settingsRecyclerView)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // Add items directly to RecyclerView
        val settingsItems = listOf(
            Triple(R.xml.ic_star, "Rate App") { /* Rate app functionality */ },
            Triple(R.xml.ic_share, "Share App") { /* Share app functionality */ },
            Triple(R.xml.ic_privacy, "Privacy Policy") { /* Privacy policy functionality */ }
        )

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val cardView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_settings, parent, false) as MaterialCardView
                return object : RecyclerView.ViewHolder(cardView) {}
            }

            override fun getItemCount() = settingsItems.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val (iconRes, text, onClick) = settingsItems[position]
                holder.itemView.findViewById<View>(R.id.settingsIcon).apply {
                    this.setBackgroundResource(iconRes)
                }
                holder.itemView.findViewById<View>(R.id.settingsText).apply {
                    (this as android.widget.TextView).text = text
                }
                holder.itemView.setOnClickListener { onClick() }
            }
        }
    }
} 