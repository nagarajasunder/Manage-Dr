package com.example.managedr.di

import com.example.managedr.database.LocalDataSource
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
    fun providesDoctorDao(datasource: LocalDataSource) = datasource.getDoctorDao()
}