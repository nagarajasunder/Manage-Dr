package com.geekydroid.managedr.application.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.geekydroid.managedr.DATA_EXPORT_COMPLELETED_NOTIFICATION_CHANNEL_ID

import com.geekydroid.managedr.DATA_EXPORT_NOTIFICATION_CHANNEL_ID
import com.geekydroid.managedr.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationBuilder @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    fun createNotification() =
        NotificationCompat.Builder(applicationContext, DATA_EXPORT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.folder)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.data_export_notification_title))
            .setOngoing(true)

    fun removeNotification(dataExportNotificationId: Int) {
        notificationManager.cancel(dataExportNotificationId)
    }

    fun showDataExportCompleteNotification() {
        val notification = NotificationCompat.Builder(applicationContext,
            DATA_EXPORT_COMPLELETED_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.check)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText("Data export successfully")
            .build()

        notificationManager.notify(DATA_EXPORT_COMPLETED_NOTIFICATION_ID,notification)
    }
}

const val DATA_EXPORT_NOTIFICATION_ID = -1
const val DATA_EXPORT_COMPLETED_NOTIFICATION_ID = -2