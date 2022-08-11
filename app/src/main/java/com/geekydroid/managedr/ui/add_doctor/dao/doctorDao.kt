package com.geekydroid.managedr.ui.add_doctor.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.add_doctor.model.Doctor
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import kotlinx.coroutines.flow.Flow

@Dao
interface doctorDao {

    @Insert
    suspend fun insertNewDoctor(newDoctor: Doctor)

    //Data to be displayed in the home screen
    @Query("SELECT doctor_id as doctorID, " +
            "doctor_name as doctorName, " +
            "hospital_name as hospitalName, " +
            "specialization as specialization, " +
            "created_on as createdOn, " +
            "updated_on as updatedOn FROM DOCTOR")
    fun getDoctorDataWithTxs(): Flow<List<HomeScreenDoctorData>>


}