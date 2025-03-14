package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
import java.util.Date

/**
 * Manages the daily credit system for API calls
 */
class CreditManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    
    /**
     * Get the remaining credits for today
     * @return Number of remaining credits
     */
    fun getRemainingCredits(): Int {
        val currentDate = getCurrentDateString()
        val lastResetDate = sharedPreferences.getString(KEY_LAST_RESET_DATE, "") ?: ""
        
        // If it's a new day, reset credits
        if (currentDate != lastResetDate) {
            resetDailyCredits(currentDate)
        }
        
        return sharedPreferences.getInt(KEY_REMAINING_CREDITS, DEFAULT_DAILY_CREDITS)
    }
    
    /**
     * Use one credit
     * @return true if credit was successfully used, false if no credits remaining
     */
    fun useCredit(): Boolean {
        val currentCredits = getRemainingCredits()
        
        if (currentCredits <= 0) {
            return false
        }
        
        sharedPreferences.edit()
            .putInt(KEY_REMAINING_CREDITS, currentCredits - 1)
            .apply()
            
        return true
    }
    
    /**
     * Reset daily credits
     * @param currentDate Current date string in format YYYY-MM-DD
     */
    private fun resetDailyCredits(currentDate: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_RESET_DATE, currentDate)
            .putInt(KEY_REMAINING_CREDITS, DEFAULT_DAILY_CREDITS)
            .apply()
    }
    
    /**
     * Get current date as string in format YYYY-MM-DD
     */
    private fun getCurrentDateString(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        return "$year-$month-$day"
    }
    
    companion object {
        private const val PREFERENCES_NAME = "ielts_credit_preferences"
        private const val KEY_REMAINING_CREDITS = "remaining_credits"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        private const val DEFAULT_DAILY_CREDITS = 3 // Default daily credit limit
    }
} 