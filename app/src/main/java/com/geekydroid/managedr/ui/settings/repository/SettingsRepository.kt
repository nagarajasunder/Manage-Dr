package com.geekydroid.managedr.ui.settings.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.addnewservice.dao.CategoryDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationScope private val externalScope:CoroutineScope,
    @IoDispatcher private val externalDispatcher:CoroutineDispatcher,
    private val serviceDao: ServiceDao,
    private val cityDao:CityDao,
    private val categoryDao: CategoryDao
    ) {

    suspend fun getDataForExport() = serviceDao.getDataForExport()
    suspend fun getCityNames() = serviceDao.getCityNames()
    suspend fun getTransactionCount() = serviceDao.getTransactionCount()

    fun getEditableData(type: String) = categoryDao.getSettingsEditableData(type)
    suspend fun updateCity(city: MdrCity) =  externalScope.launch(externalDispatcher) {
        cityDao.updateCity(city)
    }.join()

    suspend fun updateDivision(division: MdrCategory) = externalScope.launch(externalDispatcher) {
        categoryDao.updateCategory(division)
    }.join()

    suspend fun deleteCityWithTransactions(id: Int) = externalScope.launch(externalDispatcher){
        cityDao.deleteCityWithTxs(id)
    }.join()

    suspend fun deleteDivisionWithTransactions(id: Int) = externalScope.launch(externalDispatcher) {
        categoryDao.deleteCategoryWithTxs(id)
    }.join()
}