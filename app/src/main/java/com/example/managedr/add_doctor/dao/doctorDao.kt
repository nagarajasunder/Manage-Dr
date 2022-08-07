package com.example.managedr.add_doctor.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.managedr.add_doctor.model.Doctor

@Dao
interface doctorDao {

    @Insert
    suspend fun insertNewDoctor(newDoctor:Doctor)
}