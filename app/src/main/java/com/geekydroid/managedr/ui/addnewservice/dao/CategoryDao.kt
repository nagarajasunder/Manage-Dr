package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertNewCategory(vararg category: MdrCategory)
}