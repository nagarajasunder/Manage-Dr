package com.geekydroid.managedr.ui.doctordashboard.model

import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.providers.DateFormatProvider
import com.geekydroid.managedr.utils.DateUtils
import java.util.*

class DoctorDashboardTxData(
    val transactionId:Int,
    val transactionType:String,
    val transactionDate: Date,
    val transactionAmount:Int,
    val doctorId:Int,
    val divisionId:Int,
    val divisionName:String,
    val cityId:Int,
    val cityName:String,
    val createdOn:Long,
    val updatedOn:Long
) : ScreenData()
{
    val transactionDateFormatted:String
        get() = DateUtils.fromLongToDateString(transactionDate.time,DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
}