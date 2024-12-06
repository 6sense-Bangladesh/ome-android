package com.ome.app.presentation

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService() {
    private lateinit var pinpointManager: PinpointManager
    override fun onCreate() {
        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(notificationChannel)
        }*/
        pinpointManager = PinpointManager(PinpointConfiguration(
            applicationContext,
            AWSMobileClient.getInstance(),
            AWSConfiguration(applicationContext) ))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body

        Log.d(TAG, "From: ${remoteMessage.from}")
        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let { Log.d(TAG, "Message data payload: " + remoteMessage.data) }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let { Log.d(TAG, "Message Notification Body: ${it.body}") }

        /**custom notification with channelID, title, body**/
        /*        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title.toString())
                .setSmallIcon(R.drawable.ic_stove_fill)
                .setContentText(body.toString())

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) NotificationManagerCompat.from(this).notify(randomId, notificationBuilder.build())*/

        /**pinpoint notification*/
        NotificationDetails.builder().from(remoteMessage.from)
            .mapData(remoteMessage.data)
            .intentAction(NotificationClient.FCM_INTENT_ACTION)
            .build().run { pinpointManager.pinpointContext.notificationClient.handleNotificationReceived(this) }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        super.onNewToken(token)
    }

    companion object {
        private const val TAG = "PushNotificationService"
    }
}