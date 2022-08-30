package com.geekydroid.managedr.ui.add_doctor.repository

import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.model.SortPreferences
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class HomeFragmentRepository @Inject constructor(private val dao:doctorDao) {

    fun getDoctorData(searchText:String = "",preferences: String): Flow<List<HomeScreenDoctorData>> = dao.getDoctorDataBasedOnFilters(searchText,preferences)
}