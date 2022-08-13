package com.geekydroid.managedr.ui.add_doctor.model

import com.geekydroid.managedr.application.ScreenData
import java.text.DateFormat

class HomeScreenDoctorData(
    val doctorID: Int = 0,
    val doctorName: String = "",
    val hospitalName: String = "",
    val doctorMobileNumber:String = "",
    val specialization: String = "",
    val createdOn: Long = System.currentTimeMillis(),
    val updatedOn: Long = System.currentTimeMillis()
) : ScreenData() {

    val createdOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOn)

}