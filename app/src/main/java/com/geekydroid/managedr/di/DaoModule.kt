package com.geekydroid.managedr.di

import com.geekydroid.managedr.database.LocalDataSource
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun providesDoctorDao(
        datasource: LocalDataSource):doctorDao = datasource.getDoctorDao()
}