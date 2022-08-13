package com.geekydroid.managedr.ui.addnewservice.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class AddNewServiceRepository @Inject constructor(
    private val doctorDao: doctorDao,
    private val cityDao: CityDao,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val externalDispatcher: CoroutineDispatcher,
) {

    fun getDoctorName(doctorId: Int) = doctorDao.getDoctorName(doctorId)

    fun getAllCityNames() = cityDao.getAllCityNames()
    suspend fun addNewCity(city: MdrCity) = externalScope.launch(externalDispatcher) {
        cityDao.insertNewCity(city)
    }.join()


}
