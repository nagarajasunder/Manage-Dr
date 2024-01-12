package com.geekydroid.managedr.di

import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsDoctorRespository(
        doctorRepositoryImpl: DoctorRepositoryImpl
    ) : DoctorRepository

}