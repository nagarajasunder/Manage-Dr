package com.geekydroid.managedr.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.Doctor

@Database(entities = [Doctor::class], version = 1)
abstract class LocalDataSource : RoomDatabase() {

    abstract fun getDoctorDao(): doctorDao
}