package com.geekydroid.managedr.ui.add_doctor.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import kotlinx.coroutines.flow.Flow

@Dao
interface doctorDao {

    @Insert
    suspend fun insertNewDoctor(newMdrDoctor: MdrDoctor)

    //Data to be displayed in the home screen
    @Query(
        "SELECT doctor_id as doctorID, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "specialization as specialization, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR"
    )
    fun getDoctorDataWithTxs(): Flow<List<HomeScreenDoctorData>>

    //Data to be displayed in the doctor dashboard
    @Query(
        "SELECT doctor_id as doctorID, " +
                "doctor_name as doctorName, " +
                "hospital_name as hospitalName, " +
                "specialization as specialization, " +
                "doctor_mobile_number as doctorMobileNumber, " +
                "created_on as createdOn, " +
                "updated_on as updatedOn FROM MDR_DOCTOR WHERE doctor_id = :doctorId"
    )
    fun getDoctorDataByDoctorId(doctorId: Int): Flow<HomeScreenDoctorData>

    @Query("SELECT doctor_name FROM MDR_DOCTOR WHERE doctor_id = :doctorId")
    fun getDoctorName(doctorId: Int): Flow<String>


}