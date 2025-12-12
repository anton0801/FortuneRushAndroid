package com.appslocraapp.slotscrashapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.collections.contains

class AppPushService : FirebaseMessagingService() {

    companion object {
        private const val FORTUNE_RUSH_CHANNEL_ID = "fortune_casino_notifications"
        private const val FORTUNE_RUSH_CHANNEL_NAME = "Fortune Rush Casino Notifications"
        private const val FORTUNE_RUSH_NOT_TAG = "FORTUNE_RUSH_CAS"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                eggLabelShowNotification(it.title ?: FORTUNE_RUSH_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                eggLabelShowNotification(it.title ?: FORTUNE_RUSH_NOT_TAG, it.body ?: "", data = null)
            }
        }

        if (remoteMessage.data.isNotEmpty()) {
            eggLabelHandleDataPayload(remoteMessage.data)
        }
    }

    private fun eggLabelShowNotification(title: String, message: String, data: String?) {
        val eggLabelNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FORTUNE_RUSH_CHANNEL_ID,
                FORTUNE_RUSH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            eggLabelNotificationManager.createNotificationChannel(channel)
        }

        val eggLabelIntent = Intent(this, StartActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val eggLabelPendingIntent = PendingIntent.getActivity(
            this,
            0,
            eggLabelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val eggLabelNotification = NotificationCompat.Builder(this, FORTUNE_RUSH_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(eggLabelPendingIntent)
            .build()

        eggLabelNotificationManager.notify(System.currentTimeMillis().toInt(), eggLabelNotification)
    }

    private fun eggLabelHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(FORTUNE_RUSH_CHANNEL_ID, "Data key=$key value=$value")
        }
    }

}