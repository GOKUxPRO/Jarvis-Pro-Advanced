package com.jarvis.assistant.monitor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class BackgroundMonitor(private val context: Context) {
    
    // 6. Autonomous Background Monitor
    fun checkSystemStatus() {
        val batteryLevel = getBatteryLevel()
        if (batteryLevel < 15) {
            sendNotification("Battery Low", "Boss, battery 15% thi niche aavi gayi che.")
        }
    }

    private fun getBatteryLevel(): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
        return bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun sendNotification(title: String, msg: String) {
        val chId = "jarvis_monitor"
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mgr.createNotificationChannel(NotificationChannel(chId, "Monitor", NotificationManager.IMPORTANCE_HIGH))
        }
        val notif = NotificationCompat.Builder(context, chId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(msg)
            .build()
        mgr.notify(2, notif)
    }
}

// 7. Advanced Code Assistant Prompt
object CodeAssistant {
    fun getCodePrompt(userQuery: String): String {
        return "You are an expert programmer. Provide only the code solution without explanations for: $userQuery"
    }
}
