package com.jarvis.assistant.agent

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class AiOrchestrator(private val context: Context) {
    
    // 5. Local + Cloud Intelligent Routing
    fun routePrompt(prompt: String, isSensitive: Boolean): String {
        if (isSensitive) return "LOCAL" // Privacy first
        if (!isInternetAvailable()) return "LOCAL"
        
        // If prompt is complex or asks for deep research, use CLOUD
        if (prompt.length > 50 || prompt.contains("search") || prompt.contains("research")) {
            return "CLOUD"
        }
        
        // Default to local for fast, simple commands
        return "LOCAL"
    }

    private fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
