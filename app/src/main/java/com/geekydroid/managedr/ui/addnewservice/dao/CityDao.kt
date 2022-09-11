package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.*
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert
    suspend fun insertNewCity(vararg city: MdrCity)

    @Query("SELECT * FROM MDR_CITY")
    fun getAllCities(): Flow<List<MdrCity>>

    @Update
    suspend fun updateCity(city: MdrCity)

    @Transaction
    suspend fun deleteCityWithTxs(id: Int) {
        deleteCityById(id)
        deleteCityTransactionsById(id)
    }

    @Query("DELETE FROM MDR_SERVICE WHERE city_id = :id")
    suspend fun deleteCityTransactionsById(id: Int)

    @Query("DELETE FROM MDR_CITY WHERE city_id = :id")
    suspend fun deleteCityById(id:Int)

    @Query("SELECT city_name FROM MDR_CITY")
    fun getAllCityNames(): Flow<List<String>>
}