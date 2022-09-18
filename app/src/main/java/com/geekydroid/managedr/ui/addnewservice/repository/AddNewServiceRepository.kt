package com.geekydroid.managedr.ui.addnewservice.repository

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.addnewservice.dao.CategoryDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class AddNewServiceRepository @Inject constructor(
    private val doctorDao: doctorDao,
    private val cityDao: CityDao,
    private val serviceDao:ServiceDao,
    private val categoryDao: CategoryDao,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val externalDispatcher: CoroutineDispatcher,
) {

    fun getDoctorName(doctorId: Int) = doctorDao.getDoctorName(doctorId)


    suspend fun addNewCity(city: MdrCity) = externalScope.launch(externalDispatcher) {
        cityDao.insertNewCity(city)
    }.join()

    suspend fun addNewService(newService: MdrService) = externalScope.launch(externalDispatcher) {
        serviceDao.addNewService(newService)
    }.join()

    fun getAllDivisionNames():Flow<List<MdrCategory>> = categoryDao.getAllCategories()
    suspend fun addNewCategory(newCategory: MdrCategory) = externalScope.launch(externalDispatcher) {
        categoryDao.insertNewCategory(newCategory)
    }.join()

    suspend fun deleteTransaction(existingTransactionId: Int) = externalScope.launch(externalDispatcher) {
        serviceDao.deleteTransactionById(existingTransactionId)
    }.join()

    fun getTransactionById(transactionId: Int) = serviceDao.getTransactionDetailsById(transactionId)
    suspend fun updateService(serviceDetails: MdrService) = externalScope.launch(externalDispatcher) {
        serviceDao.updateService(serviceDetails)
    }

}
