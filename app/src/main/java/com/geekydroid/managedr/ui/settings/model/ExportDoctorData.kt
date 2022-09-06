package com.geekydroid.managedr.ui.settings.model

import java.util.*

data class ExportDoctorData(
    val transactionId: Int = 0,
    val doctorName: String = "",
    val transactionType: String = "",
    val transactionDate: Date? = null,
    val transactionAmount: Double = 0.0,
    val divisionName: String = "",
    val cityName: String = "",
    val createdOn: Long = System.currentTimeMillis(),
    val updatedOn: Long = System.currentTimeMillis(),
)