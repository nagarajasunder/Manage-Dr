package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class DoctorRepository @Inject constructor(
    private val dao: doctorDao,
    private val cityDao:CityDao,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun addNewDoctor(mdrDoctor: MdrDoctor) = externalScope.launch(dispatcher) {
        dao.insertNewDoctor(mdrDoctor)
    }.join()

    fun getDoctorById(doctorId: Int) = dao.getDoctorById(doctorId)
    suspend fun updateDoctor(doctor: MdrDoctor) = dao.updateDoctorAndTransactions(doctor)
    suspend fun addNewCity(newCity: MdrCity) = cityDao.insertNewCity(newCity)
    fun getAllCities() : Flow<List<MdrCity>> = cityDao.getAllCities()

}