package com.geekydroid.managedr

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.HiltAndroidApp

const val DATA_EXPORT_NOTIFICATION_CHANNEL_ID = "Data export channel"
const val DATA_EXPORT_COMPLELETED_NOTIFICATION_CHANNEL_ID = "Data export completed"

@HiltAndroidApp
class ManageDrApp : Application() {

    //This init is for Apace POI library
    init {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val dataExportChannel = NotificationChannelCompat.Builder(DATA_EXPORT_NOTIFICATION_CHANNEL_ID,NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(DATA_EXPORT_NOTIFICATION_CHANNEL_ID)
            .setDescription(applicationContext.getString(R.string.data_export_channel_description))
            .build()

        val dataExportCompletedChannel = NotificationChannelCompat.Builder(DATA_EXPORT_COMPLELETED_NOTIFICATION_CHANNEL_ID,NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName(DATA_EXPORT_COMPLELETED_NOTIFICATION_CHANNEL_ID)
            .setDescription(applicationContext.getString(R.string.data_export_completed_channel_description))
            .build()

        NotificationManagerCompat.from(applicationContext).createNotificationChannelsCompat(listOf(dataExportChannel,dataExportCompletedChannel))
    }

}