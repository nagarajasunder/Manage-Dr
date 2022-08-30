package com.geekydroid.managedr.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.addnewservice.dao.CategoryDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import com.geekydroid.managedr.utils.Convertors

@Database(entities = [MdrDoctor::class,MdrService::class,MdrCity::class,MdrCategory::class], version = 1)
@TypeConverters(Convertors::class)
abstract class LocalDataSource : RoomDatabase() {

    abstract fun getMdrDoctorDao(): doctorDao

    abstract fun getMdrServiceDao():ServiceDao

    abstract fun getMdrCategoryDao():CategoryDao

    abstract fun getMdrCityDao():CityDao
}