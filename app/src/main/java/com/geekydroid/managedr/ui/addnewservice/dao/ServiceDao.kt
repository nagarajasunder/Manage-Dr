package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
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
            "ON (`C`.city_id == `S`.city_id) WHERE `S`.serviced_doctor_id = :doctorId")
    fun getTxBasedOnFilters(doctorId:Int):Flow<List<DoctorDashboardTxData>>
}