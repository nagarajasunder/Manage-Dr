package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class DoctorRepository @Inject constructor(
    private val dao: doctorDao,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun addNewDoctor(mdrDoctor: MdrDoctor) = externalScope.launch(dispatcher) {
        dao.insertNewDoctor(mdrDoctor)
    }.join()

    fun getDoctorById(doctorId: Int) = dao.getDoctorById(doctorId)
    suspend fun updateDoctor(doctor: MdrDoctor) = dao.updateDoctor(doctor)

}