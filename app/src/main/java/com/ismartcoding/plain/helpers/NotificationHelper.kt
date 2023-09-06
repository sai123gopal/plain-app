package com.ismartcoding.plain.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.ismartcoding.lib.extensions.notificationManager
import com.ismartcoding.plain.Constants
import com.ismartcoding.plain.MainApp
import com.ismartcoding.plain.R
import com.ismartcoding.plain.features.locale.LocaleHelper.getString
import com.ismartcoding.plain.receivers.ServiceStopBroadcastReceiver
import com.ismartcoding.plain.ui.MainActivity

object NotificationHelper {
    fun createContentIntent(context: Context): PendingIntent {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return if (launchIntent != null) {
            launchIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            PendingIntent.getActivity(
                context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val fallbackIntent = Intent(context, MainActivity::class.java)
            PendingIntent.getActivity(
                context, 0, fallbackIntent, PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    fun ensureDefaultChannel() {
        val notificationManager = MainApp.instance.notificationManager
        if (notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT).apply {
                setShowBadge(false)
            })
        }
    }

    fun createServiceNotification(context: Context, action: String, title: String): Notification {
        val stopPendingIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, ServiceStopBroadcastReceiver::class.java).apply {
                this.action = action
            }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID).apply {
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification))
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(title)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOnlyAlertOnce(true)
            setSilent(true)
            setWhen(System.currentTimeMillis())
            setAutoCancel(false)
            setContentIntent(createContentIntent(context))
            addAction(-1, getString(R.string.stop_service), stopPendingIntent)
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
        }.build()
    }
}