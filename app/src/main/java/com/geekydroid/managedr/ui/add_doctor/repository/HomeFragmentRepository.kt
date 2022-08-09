package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class HomeFragmentRepository @Inject constructor(private val dao:doctorDao) {

    fun getDoctorData() = dao.getDoctorDataWithTxs()
}