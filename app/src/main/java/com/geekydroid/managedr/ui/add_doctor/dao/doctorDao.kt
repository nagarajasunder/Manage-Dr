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
    suspend fun insertNewDoctor(newDoctor:Doctor)

    //Data to be displayed in the home screen
    @Query("SELECT * FROM DOCTOR")
    fun getDoctorDataWithTxs():Flow<List<HomeScreenDoctorData>>
}