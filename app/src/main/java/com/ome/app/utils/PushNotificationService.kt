package com.ome.app.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        Log.d(PushNotificationService.TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            Log.d(PushNotificationService.TAG, "Message data payload: " + remoteMessage.data)

        }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(PushNotificationService.TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(PushNotificationService.TAG, "Refreshed token: $token")
        super.onNewToken(token)
    }

    companion object {
        private const val TAG = "PushNotificationService"
    }
}