package com.geekydroid.managedr.di

import android.app.Application
import androidx.room.Room
import com.geekydroid.managedr.database.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesLocalDataSource(app: Application): LocalDataSource =
        Room.databaseBuilder(app, LocalDataSource::class.java, "manage_dr.db").build()
}