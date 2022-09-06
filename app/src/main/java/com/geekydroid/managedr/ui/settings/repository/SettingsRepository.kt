package com.geekydroid.managedr.ui.settings.repository

import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import javax.inject.Inject


class SettingsRepository @Inject constructor(private val serviceDao: ServiceDao) {

    suspend fun getDataForExport() = serviceDao.getDataForExport()
    suspend fun getCityNames() = serviceDao.getCityNames()

}