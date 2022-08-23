package com.geekydroid.managedr.ui.doctordashboard.model

import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.providers.DateFormatProvider
import com.geekydroid.managedr.utils.DateUtils
import java.util.*

data class DoctorDashboardTxData(
    val transactionId: Int = 0,
    val transactionType: String = "",
    val transactionDate: Date? = null,
    val transactionAmount: Int = 0,
    val divisionName: String = "",
    val cityName: String = "",
    val createdOn: Long = System.currentTimeMillis(),
    val updatedOn: Long = System.currentTimeMillis(),
) : ScreenData() {
    val transactionDateFormatted: String
        get() = transactionDate?.let {
            DateUtils.fromLongToDateString(it.time, DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
        } ?: ""
}