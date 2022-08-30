package com.geekydroid.managedr.ui.add_doctor.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName = "MDR_DOCTOR")
data class MdrDoctor(
    @ColumnInfo(name = "doctor_id")
    @PrimaryKey(autoGenerate = true)
    val doctorID: Int = 0,
    @ColumnInfo(name = "doctor_name")
    val doctorName: String = "",
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Date? = null,
    @ColumnInfo(name = "wedding_anniversary_date")
    val weddingAnniversaryDate: Date? = null,
    @ColumnInfo(name = "doctor_mobile_number")
    val doctorMobileNumber: String = "",
    @ColumnInfo(name = "hospital_name")
    val hospitalName: String = "",
    @ColumnInfo(name = "city_id")
    val cityId: Int = 0,
    @ColumnInfo(name = "created_on")
    val createdOn: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    val updatedOn: Long = System.currentTimeMillis(),
) {
    val createdOnFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOn)
}