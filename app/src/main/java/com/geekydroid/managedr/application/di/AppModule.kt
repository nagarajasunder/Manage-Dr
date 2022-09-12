package com.geekydroid.managedr.application.di

import android.app.Application
import com.geekydroid.managedr.ManageDrApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesApp(app:Application):ManageDrApp = (app as ManageDrApp)
}