package com.schoolnav.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class SchoolNavApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createDefaultNotificationChannel()
    }

    private fun createDefaultNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channelId = getString(R.string.default_notification_channel_id)
        if (manager.getNotificationChannel(channelId) != null) return
        val channel = NotificationChannel(
            channelId,
            getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.default_notification_channel_description)
            enableLights(true)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }
}
