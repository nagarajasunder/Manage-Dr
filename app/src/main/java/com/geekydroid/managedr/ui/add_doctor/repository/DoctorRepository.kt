package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import kotlinx.coroutines.flow.Flow

interface DoctorRepository {

    suspend fun addNewDoctor(mdrDoctor: MdrDoctor) : Unit

    fun getDoctorById(doctorId: Int) : Flow<MdrDoctor>
    suspend fun updateDoctor(doctor: MdrDoctor) : Unit
    suspend fun addNewCity(newCity: MdrCity) : Unit
    fun getAllCities() : Flow<List<MdrCity>>

}