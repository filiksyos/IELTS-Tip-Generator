package com.example.presentation

import android.app.Application
import android.util.Log
import com.example.presentation.DI.appModule
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.Arrays

class BaseClass : Application() {
    private val TAG = "IELTS_BaseClass"

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BaseClass)
            androidLogger(Level.DEBUG)
            modules(appModule)
        }

        // Initialize the Mobile Ads SDK
        Log.d(TAG, "Initializing AdMob SDK")
        MobileAds.initialize(this) { initializationStatus ->
            // Log the initialization status
            val statusMap = initializationStatus.adapterStatusMap
            for ((adapterClass, status) in statusMap) {
                Log.d(TAG, "Adapter: $adapterClass, Status: ${status.initializationState}, " +
                        "Description: ${status.description}")
            }
            Log.d(TAG, "AdMob SDK initialized")
        }

        // Set up test devices (optional, for testing)
        val testDeviceIds = Arrays.asList("ABCDEF012345") // Replace with your test device ID
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        Log.d(TAG, "AdMob test device configuration set")
    }
}
