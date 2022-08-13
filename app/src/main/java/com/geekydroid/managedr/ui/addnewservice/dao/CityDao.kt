package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert
    suspend fun insertNewCity(vararg city: MdrCity)

    @Query("SELECT city_name FROM MDR_CITY")
    fun getAllCityNames(): Flow<List<String>>
}