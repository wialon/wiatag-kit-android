package com.gurtam.wiatag_kit.example.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gurtam.wiatag_kit.example.MainActivity
import com.gurtam.wiatag_kit.example.R
import com.gurtam.wiatagkit.MessageManager


class PushService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            /**handle fcm message*/
            MessageManager.handleFcmMessage(data)
            /**yor should show notification on every received message, see Android P limitation */
            addNotification("new message received")
        }
    }

    override fun onNewToken(s: String) {
        /**register fcm token*/
        MessageManager.registerFcmToken(this, s)
    }

    /**helper function to show notification */
    private fun addNotification(message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Default"
        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_input)
                .setContentText(message).setAutoCancel(true).setContentIntent(pendingIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }
}