package com.example.managedr.add_doctor.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DOCTOR", primaryKeys = ["doctor_id"])
data class Doctor(
    @ColumnInfo(name = "doctor_id")
    @PrimaryKey(autoGenerate = true)
    var doctorID:Int = 0,
    @ColumnInfo(name = "doctor_name")
    var doctorName:String = "",
    @ColumnInfo(name = "hospital_name")
    var hospitalName:String = "",
    @ColumnInfo(name = "specialization")
    var specialization:String = "",
    @ColumnInfo(name = "created_on")
    var createdOn:Long = 0L,
    @ColumnInfo("updated_on")
    var updatedOn:Long = 0L
)