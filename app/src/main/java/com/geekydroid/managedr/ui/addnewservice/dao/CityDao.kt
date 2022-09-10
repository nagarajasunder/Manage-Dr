package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert
    suspend fun insertNewCity(vararg city: MdrCity)

    @Query("SELECT * FROM MDR_CITY")
    fun getAllCities(): Flow<List<MdrCity>>
}