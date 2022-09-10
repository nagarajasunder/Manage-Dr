package com.geekydroid.managedr.ui.settings.repository

import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.addnewservice.dao.CategoryDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import com.geekydroid.managedr.ui.settings.model.SettingsEditType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepository @Inject constructor(
    private val serviceDao: ServiceDao,
    private val cityDao:CityDao,
    private val categoryDao: CategoryDao
    ) {

    suspend fun getDataForExport() = serviceDao.getDataForExport()
    suspend fun getCityNames() = serviceDao.getCityNames()
    suspend fun getTransactionCount() = serviceDao.getTransactionCount()

    fun getEditableData(type: String) = categoryDao.getSettingsEditableData(type)
}