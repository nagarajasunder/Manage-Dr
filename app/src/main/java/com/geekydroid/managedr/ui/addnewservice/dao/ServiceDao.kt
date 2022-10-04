package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import androidx.sqlite.db.SupportSQLiteQuery
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import com.geekydroid.managedr.ui.doctordashboard.model.DashboardFilters
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.settings.model.ExportDoctorData
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Insert(onConflict = REPLACE)
    fun addNewService(vararg MDRService: MdrService)

    @Query("SELECT `S`.service_id as transactionId, " +
            "`S`.transaction_type as transactionType, " +
            "`S`.service_date as transactionDate, " +
            "`S`.service_amount as transactionAmount, " +
            "`D`.category_name as divisionName, " +
            "`C`.city_name as cityName, " +
            "`S`.created_on as createdOn, " +
            "`S`.updated_on as updatedOn" +
            " FROM MDR_SERVICE `S` " +
            "LEFT JOIN MDR_CATEGORY `D` " +
            "ON (`D`.category_id == `S`.category_id) " +
            "LEFT JOIN MDR_CITY `C` " +
            "ON (`C`.city_id == `S`.city_id) WHERE `S`.serviced_doctor_id = :doctorId ORDER BY `S`.created_on DESC")
    fun getTxBasedOnFilters(doctorId:Int):Flow<List<DoctorDashboardTxData>>

    @RawQuery(observedEntities = [MdrService::class])
    fun temp(query: SupportSQLiteQuery):Flow<List<DoctorDashboardTxData>>

    @Query("SELECT `S`.service_id as transactionId, " +
            "`DR`.doctor_name as doctorName, " +
            "`S`.transaction_type as transactionType, " +
            "`S`.service_date as transactionDate, " +
            "`S`.service_amount as transactionAmount, " +
            "`D`.category_name as divisionName, " +
            "`C`.city_name as cityName, " +
            "`S`.created_on as createdOn, " +
            "`S`.updated_on as updatedOn" +
            " FROM MDR_SERVICE `S` " +
            "LEFT JOIN MDR_DOCTOR `DR` " +
            "ON (`S`.serviced_doctor_id == `DR`.doctor_id) "+
            "LEFT JOIN MDR_CATEGORY `D` " +
            "ON (`S`.category_id == `D`.category_id) " +
            "LEFT JOIN MDR_CITY `C` " +
            "ON (`S`.city_id == `C`.city_id) ORDER BY `S`.created_on ASC")
    suspend fun getDataForExport(): List<ExportDoctorData>

    @Query("SELECT DISTINCT(`C`.city_name) FROM MDR_SERVICE `S` LEFT JOIN MDR_CITY `C` WHERE `C`.city_id == `S`.city_id")
    suspend fun getCityNames(): List<String>

    @Query("SELECT COUNT(service_id) FROM MDR_SERVICE")
    suspend fun getTransactionCount():Int

    @Query("DELETE FROM MDR_SERVICE WHERE service_id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Int)

    @Query("SELECT * FROM MDR_SERVICE WHERE service_id = :transactionId")
    fun getTransactionDetailsById(transactionId: Int):Flow<MdrService>

    @Update
    suspend fun updateService(serviceDetails: MdrService)
}