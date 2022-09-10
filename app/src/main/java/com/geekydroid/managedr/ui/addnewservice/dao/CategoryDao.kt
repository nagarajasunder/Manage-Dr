package com.geekydroid.managedr.ui.addnewservice.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertNewCategory(vararg category: MdrCategory)

    @Query("SELECT * FROM MDR_CATEGORY")
    fun getAllCategories(): Flow<List<MdrCategory>>

    @Query("SELECT * FROM (" +
            "SELECT category_id as id, category_name as name, created_on as createdOn, updated_on as updatedOn, 'EDIT_TYPE_DIVISION' as type FROM MDR_CATEGORY" +
            " UNION ALL " +
            " SELECT city_id as id, city_name as name, created_on as createdOn, updated_on as updatedOn, 'EDIT_TYPE_CITY' as type FROM MDR_CITY) as data WHERE type LIKE :editType")
    fun getSettingsEditableData(editType:String): Flow<List<SettingsEditData>>
}