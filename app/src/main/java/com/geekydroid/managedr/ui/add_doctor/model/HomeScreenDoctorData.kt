package com.geekydroid.managedr.ui.add_doctor.model

import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.providers.DateFormatProvider
import com.geekydroid.managedr.utils.DateUtils
import java.lang.reflect.Array.get
import java.text.DateFormat

data class HomeScreenDoctorData(
    val doctorID: Int = 0,
    val cityId:Int = 0,
    val doctorName: String = "",
    val hospitalName: String = "",
    val doctorMobileNumber:String = "",
    val doctorDateOfBirth:Long = 0L,
    val doctorWeddingDate:Long = 0L,
    val createdOn: Long = System.currentTimeMillis(),
    val updatedOn: Long = System.currentTimeMillis()
) : ScreenData() {

    val createdOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOn)

    val weddingDateText: String
        get() = if(doctorWeddingDate == 0L) "" else DateUtils.fromLongToDateString(doctorWeddingDate,
            DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
    val dateOfBirthText: String
        get() = if (doctorDateOfBirth == 0L) "" else DateUtils.fromLongToDateString(doctorDateOfBirth,
            DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)

}