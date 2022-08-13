package com.geekydroid.managedr.di

import com.geekydroid.managedr.database.LocalDataSource
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.addnewservice.dao.CategoryDao
import com.geekydroid.managedr.ui.addnewservice.dao.CityDao
import com.geekydroid.managedr.ui.addnewservice.dao.ServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun providesDoctorDao(
        datasource: LocalDataSource):doctorDao = datasource.getMdrDoctorDao()

    @Provides
    @Singleton
    fun providesServiceDao(datasource: LocalDataSource):ServiceDao = datasource.getMdrServiceDao()

    @Provides
    @Singleton
    fun providesCategoryDao(datasource: LocalDataSource):CategoryDao = datasource.getMdrCategoryDao()

    @Provides
    @Singleton
    fun providesCityDao(datasource: LocalDataSource):CityDao = datasource.getMdrCityDao()
}