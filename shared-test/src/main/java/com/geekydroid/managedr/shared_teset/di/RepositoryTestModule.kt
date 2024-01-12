package com.geekydroid.managedr.shared_teset.di

import com.geekydroid.managedr.di.RepositoryModule
import com.geekydroid.managedr.shared_teset.data.source.local.FakeDoctorRepository
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object RepositoryTestModule {

    @Singleton
    @Provides
    fun provideDoctorRepository() : DoctorRepository  {
        return FakeDoctorRepository()
    }

}