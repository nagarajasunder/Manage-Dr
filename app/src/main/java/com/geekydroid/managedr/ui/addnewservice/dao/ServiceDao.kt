package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import com.geekydroid.managedr.ui.addnewservice.model.MdrService

@Dao
interface ServiceDao {

    @Insert(onConflict = REPLACE)
    fun addNewService(vararg MDRService: MdrService)
}