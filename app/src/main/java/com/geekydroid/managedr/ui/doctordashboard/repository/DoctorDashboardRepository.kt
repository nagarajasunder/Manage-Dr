package com.geekydroid.managedr.ui.doctordashboard.repository

import com.geekydroid.managedr.database.LocalDataSource
import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class DoctorDashboardRepository @Inject constructor(
    private val datasource: LocalDataSource,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @IoDispatcher
    private val externalDispatcher:CoroutineDispatcher
) {

    fun getDoctorData(doctorId:Int):Flow<HomeScreenDoctorData> = datasource.getMdrDoctorDao().getDoctorDataByDoctorId(doctorId)

}