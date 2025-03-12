package com.example.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.data.preferences.PreferencesManager
import com.example.presentation.utils.NetworkUtils
import com.example.presentation.utils.NoConnectionDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val preferencesManager: PreferencesManager by inject()
    private lateinit var bottomNav: BottomNavigationView
    private var noConnectionDialog: NoConnectionDialog? = null
    
    // Delay showing the no connection dialog to ensure splash screen animation completes
    private val CONNECTION_CHECK_DELAY = 500L // 500ms delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Get the nested graph and set its start destination
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        val mainGraph = navGraph.findNode(R.id.main_navigation) as NavGraph
        mainGraph.setStartDestination(R.id.navigation_get_tip)
        navController.graph = navGraph
        
        // Delay checking for internet connectivity to ensure splash screen animation completes
        Handler(Looper.getMainLooper()).postDelayed({
            checkInternetConnectivity()
        }, CONNECTION_CHECK_DELAY)
    }
    
    private fun checkInternetConnectivity() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoConnectionDialog()
        }
    }
    
    private fun showNoConnectionDialog() {
        noConnectionDialog?.dismiss()
        
        noConnectionDialog = NoConnectionDialog(
            context = this,
            isServerError = false,
            onRetryClick = {
                if (NetworkUtils.isNetworkAvailable(this)) {
                    // Internet is back, dismiss the dialog
                    noConnectionDialog?.dismiss()
                } else {
                    // Still no internet, show the dialog again
                    showNoConnectionDialog()
                }
            }
        )
        
        noConnectionDialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        noConnectionDialog?.dismiss()
        noConnectionDialog = null
    }
}