package com.geekydroid.managedr.application.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.geekydroid.managedr.ManageDrApp
import com.geekydroid.managedr.application.notification.DATA_EXPORT_NOTIFICATION_ID
import com.geekydroid.managedr.application.notification.NotificationBuilder
import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.di.ServiceScope
import com.geekydroid.managedr.ui.dataExport.DataExport
import com.geekydroid.managedr.ui.settings.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class DataExportService : Service() {

    @Inject
    lateinit var application:ManageDrApp

    @Inject
    lateinit var repository: SettingsRepository

    @Inject
    @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @ServiceScope
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var notificationBuilder:NotificationBuilder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(DATA_EXPORT_NOTIFICATION_ID,notificationBuilder.createNotification().build())

        scope.launch(ioDispatcher) {
            val uri: Uri? = Uri.parse(intent?.getStringExtra("fileuri"))
            if (uri != null)
            {
                val data = repository.getDataForExport()
                val cityNames = repository.getCityNames()
                DataExport.createWorkBook(data,cityNames,application,uri)
                notificationBuilder.showDataExportCompleteNotification()
                stopSelf()
            }
            else
            {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        notificationBuilder.removeNotification(DATA_EXPORT_NOTIFICATION_ID)
    }
}