package com.example.presentation.gettip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.data.DashboardCategory
import com.example.presentation.R
import com.example.presentation.viewModel.GetTipViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GetTipFragment : Fragment() {
    private val TAG = "IELTS_GetTipFragment"
    
    private val viewModel: GetTipViewModel by sharedViewModel()
    
    private lateinit var readingOption: CardView
    private lateinit var listeningOption: CardView
    private lateinit var writingOption: CardView
    private lateinit var speakingOption: CardView
    
    // AdMob variables
    private lateinit var adViewContainer: FrameLayout
    private var adView: AdView? = null
    private val adUnitId = "ca-app-pub-3940256099942544/6300978111" // Test banner ad unit ID

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
        
        // Clear any previously generated tip when this fragment is shown
        viewModel.clearGeneratedTip()
        
        setupViews(view)
        setupClickListeners()
        setupBannerAd()
    }

    private fun setupViews(view: View) {
        Log.d(TAG, "Setting up views")
        readingOption = view.findViewById(R.id.readingOption)
        listeningOption = view.findViewById(R.id.listeningOption)
        writingOption = view.findViewById(R.id.writingOption)
        speakingOption = view.findViewById(R.id.speakingOption)
        adViewContainer = view.findViewById(R.id.ad_view_container)
        
        // Ensure the ad container is visible
        if (adViewContainer != null) {
            Log.d(TAG, "Ad container found and initialized")
            adViewContainer.visibility = View.VISIBLE
        } else {
            Log.e(TAG, "Ad container not found in layout")
        }
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
    
    private fun setupBannerAd() {
        Log.d(TAG, "Setting up banner ad")
        try {
            // Get the ad size first
            val adSize = getAdSize()
            if (adSize == null) {
                Log.e(TAG, "Could not determine ad size, using default BANNER size")
                // Continue with default size
            }
            
            // Create a new AdView
            adView = AdView(requireContext()).apply {
                adUnitId = this@GetTipFragment.adUnitId
                // Set the ad size (using BANNER as fallback if null)
                setAdSize(adSize ?: AdSize.BANNER)
            }
            
            Log.d(TAG, "AdView created with unit ID: $adUnitId")
            
            // Add the AdView to the view hierarchy
            adViewContainer.removeAllViews()
            adViewContainer.addView(adView)
            Log.d(TAG, "AdView added to container")
            
            // Create an ad request and load the ad
            val adRequest = AdRequest.Builder().build()
            Log.d(TAG, "Loading ad with request: $adRequest")
            adView?.loadAd(adRequest)
            
            // Set ad listener for events
            adView?.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG, "Ad loaded successfully")
                    // Make the container visible when ad is loaded
                    adViewContainer.visibility = View.VISIBLE
                }
                
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Ad failed to load: ${adError.message}, code: ${adError.code}, domain: ${adError.domain}")
                }
                
                override fun onAdOpened() {
                    Log.d(TAG, "Ad opened")
                }
                
                override fun onAdClosed() {
                    Log.d(TAG, "Ad closed")
                }
                
                override fun onAdClicked() {
                    Log.d(TAG, "Ad clicked")
                }
                
                override fun onAdImpression() {
                    Log.d(TAG, "Ad impression recorded")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up banner ad: ${e.message}", e)
        }
    }
    
    private fun getAdSize(): AdSize? {
        try {
            Log.d(TAG, "Calculating ad size...")
            
            // Determine the screen width to use for the ad width
            val outMetrics = resources.displayMetrics
            
            val widthPixels = outMetrics.widthPixels
            val density = outMetrics.density
            
            // Calculate the width of the ad in dp
            val adWidthPixels = widthPixels - adViewContainer.paddingLeft - adViewContainer.paddingRight
            val adWidth = (adWidthPixels / density).toInt()
            
            Log.d(TAG, "Screen width: $widthPixels px, density: $density, calculated ad width: $adWidth dp")
            
            try {
                // Try to get the adaptive banner size
                val adaptiveSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)
                if (adaptiveSize != null) {
                    Log.d(TAG, "Using adaptive banner size: ${adaptiveSize.width}x${adaptiveSize.height}")
                    return adaptiveSize
                } else {
                    Log.d(TAG, "Adaptive size returned null, using fallback")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting adaptive ad size: ${e.message}")
                // Continue to fallback
            }
            
            // Fallback to standard banner size
            Log.d(TAG, "Using fallback BANNER size")
            return AdSize.BANNER
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ad size: ${e.message}", e)
            // Ultimate fallback
            return AdSize.BANNER
        }
    }
    
    private fun navigateToInputScreen(category: DashboardCategory) {
        Log.e(TAG, "Setting selected category: $category")
        viewModel.setSelectedCategory(category)
        
        val action = R.id.action_getTipFragment_to_tipInputFragment
        Log.e(TAG, "Navigating to tip input fragment")
        findNavController().navigate(action)
    }
    
    override fun onPause() {
        adView?.pause()
        super.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        adView?.resume()
    }
    
    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }
} 