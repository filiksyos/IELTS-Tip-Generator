package com.example.presentation.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.presentation.R

/**
 * Dialog to display when there is no internet connection or server error
 */
class NoConnectionDialog(
    private val context: Context,
    private val isServerError: Boolean = false,
    private val onRetryClick: () -> Unit
) {
    private var dialog: Dialog? = null
    
    /**
     * Shows the no connection dialog
     */
    fun show() {
        if (dialog?.isShowing == true) return
        
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.layout_no_connection)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            
            // Set title and message based on error type
            val titleTextView = findViewById<TextView>(R.id.tvNoConnectionTitle)
            val messageTextView = findViewById<TextView>(R.id.tvNoConnectionMessage)
            
            if (isServerError) {
                titleTextView.text = "Server Error"
                messageTextView.text = "We're having trouble connecting to our servers. Please try again later."
            } else {
                titleTextView.text = "No Connection"
                messageTextView.text = "Couldn't contact the servers. Please check your connection and try again."
            }
            
            // Set retry button click listener
            findViewById<Button>(R.id.btnRetry).setOnClickListener {
                dismiss()
                onRetryClick()
            }
            
            show()
        }
    }
    
    /**
     * Dismisses the dialog if it's showing
     */
    fun dismiss() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}