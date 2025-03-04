package com.example.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.data.preferences.PreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val preferencesManager: PreferencesManager by inject()
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Hide bottom navigation on onboarding screen
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.onboardingFragment -> bottomNav.visibility = View.GONE
                else -> bottomNav.visibility = View.VISIBLE
            }
        }

        // Set the start destination based on whether it's the first time
        if (!preferencesManager.isFirstTime()) {
            val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
            navGraph.setStartDestination(R.id.main_navigation)
            navController.graph = navGraph
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}