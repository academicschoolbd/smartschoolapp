package com.schoolnav.app.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.schoolnav.app.MainActivity
import com.schoolnav.app.R

/**
 * Receives FCM pushes. Two things happen for every message:
 *  1. The OS-level notification is posted on the default channel so the user sees
 *     it in the system tray (and as a heads-up notification when the app is in the
 *     background).
 *  2. [FloatingNotificationCenter.post] is called so the in-app floating banner
 *     pops up while the app is in the foreground.
 */
class SchoolFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM registration token refreshed: $token")
        // TODO: send token to your backend once a backend is wired up.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.app_name)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: ""

        FloatingNotificationCenter.post(title = title, body = body)
        showSystemNotification(title = title, body = body)
    }

    private fun showSystemNotification(title: String, body: String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        private const val TAG = "SchoolFcmService"
    }
}
