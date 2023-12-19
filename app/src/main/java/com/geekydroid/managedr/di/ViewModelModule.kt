package com.geekydroid.managedr.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geekydroid.managedr.ui.add_doctor.viewmodel.AddNewDoctorViewModel
import com.geekydroid.managedr.viewmodel.ManageDrViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap

@InstallIn(ActivityRetainedComponent::class)
@Module
@Suppress("unused")
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddNewDoctorViewModel::class)
    abstract fun bindAddNewDoctorViewModel(addNewDoctorViewModel: AddNewDoctorViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory:ManageDrViewModelFactory) : ViewModelProvider.Factory
}