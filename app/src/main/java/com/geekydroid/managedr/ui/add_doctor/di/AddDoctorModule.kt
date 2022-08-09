package com.geekydroid.managedr.ui.add_doctor.di

import com.geekydroid.managedr.di.ApplicationScope
import com.geekydroid.managedr.di.IoDispatcher
import com.geekydroid.managedr.ui.add_doctor.dao.doctorDao
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddDoctorModule {

    @Provides
    @Singleton
    fun providesDoctorRepository(
        dao:doctorDao,
        @ApplicationScope externalScope: CoroutineScope,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): DoctorRepository = DoctorRepository(dao,externalScope,dispatcher)
}