package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.Doctor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoctorRepository @Inject constructor(
    private val dao: doctorDao,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun addNewDoctor(doctor: Doctor) = externalScope.launch(dispatcher) {
        dao.insertNewDoctor(doctor)
    }.join()

}