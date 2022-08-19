package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertNewCategory(vararg category: MdrCategory)

    @Query("SELECT * FROM MDR_CATEGORY")
    fun getAllCategories(): Flow<List<MdrCategory>>
}