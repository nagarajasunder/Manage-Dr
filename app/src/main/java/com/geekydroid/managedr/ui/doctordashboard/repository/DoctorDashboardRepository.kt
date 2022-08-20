package com.geekydroid.managedr.ui.doctordashboard.repository


import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class DoctorDashboardRepository @Inject constructor(
    private val doctorDao: doctorDao,
    private val cityDao: CityDao,
    private val serviceDao:ServiceDao,
    @ApplicationScope
    private val externalScope: CoroutineScope,
    @IoDispatcher
    private val externalDispatcher:CoroutineDispatcher
) {

    fun getDoctorData(doctorId:Int):Flow<HomeScreenDoctorData> = doctorDao.getDoctorDataByDoctorId(doctorId)
    fun getAllCities() = cityDao.getAllCities()
    fun getTransactionDataBasedOnFilters(doctorId: Int) = serviceDao.getTxBasedOnFilters(doctorId)

}