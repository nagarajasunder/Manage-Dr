package com.example.managedr.di

import android.app.Application
import androidx.room.Room
import com.example.managedr.database.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesLocalDataSource(@ApplicationContext app: Application) =
        Room.databaseBuilder(app.applicationContext, LocalDataSource::class.java, "manage_dr.db")
}