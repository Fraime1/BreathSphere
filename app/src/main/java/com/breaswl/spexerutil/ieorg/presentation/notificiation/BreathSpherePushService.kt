package com.breaswl.spexerutil.ieorg.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.breaswl.spexerutil.BreatheSphereActivity
import com.breaswl.spexerutil.R
import com.breaswl.spexerutil.ieorg.presentation.app.BreathSphereApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val BREATH_SPHERE_CHANNEL_ID = "breath_sphere_notifications"
private const val BREATH_SPHERE_CHANNEL_NAME = "BreathSphere Notifications"
private const val BREATH_SPHERE_CHANNEL_TAG = "BreathSphere"

class BreathSpherePushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                breathSphereShowNotification(it.title ?: BREATH_SPHERE_CHANNEL_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                breathSphereShowNotification(it.title ?: BREATH_SPHERE_CHANNEL_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            breathSphereHandleDataPayload(remoteMessage.data)
        }
    }

    private fun breathSphereShowNotification(title: String, message: String, data: String?) {
        val breathSphereNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BREATH_SPHERE_CHANNEL_ID,
                BREATH_SPHERE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            breathSphereNotificationManager.createNotificationChannel(channel)
        }

        val breathSphereIntent = Intent(this, BreatheSphereActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val breathSpherePendingIntent = PendingIntent.getActivity(
            this,
            0,
            breathSphereIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val breathSphereNotification = NotificationCompat.Builder(this, BREATH_SPHERE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_breathe_sphere_noti)
            .setAutoCancel(true)
            .setContentIntent(breathSpherePendingIntent)
            .build()

        breathSphereNotificationManager.notify(System.currentTimeMillis().toInt(), breathSphereNotification)
    }

    private fun breathSphereHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(BreathSphereApp.BREATH_SPHERE_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}