package com.geekydroid.managedr.ui.add_doctor.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geekydroid.managedr.application.ScreenData
import java.text.DateFormat

@Entity(tableName = "DOCTOR")
data class Doctor(
    @ColumnInfo(name = "doctor_id")
    @PrimaryKey(autoGenerate = true)
    val doctorID:Int = 0,
    @ColumnInfo(name = "doctor_name")
    val doctorName:String = "",
    @ColumnInfo(name = "doctor_mobile_number")
    val doctorMobileNumber:String = "",
    @ColumnInfo(name = "hospital_name")
    val hospitalName:String = "",
    @ColumnInfo(name = "specialization")
    val specialization:String = "",
    @ColumnInfo(name = "city_id")
    val cityId:Int = 0,
    @ColumnInfo(name = "created_on")
    val createdOn:Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    val updatedOn:Long = System.currentTimeMillis()
)
{
    val createdOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOn)
}