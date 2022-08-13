package com.geekydroid.managedr.ui.addnewservice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MDR_CATEGORY")
data class MdrCategory(
    @ColumnInfo(name = "category_id")
    @PrimaryKey(autoGenerate = true)
    val categoryID: Int = 0,
    @ColumnInfo(name = "category_name")
    val categoryName: String = "",
    @ColumnInfo(name = "created_on")
    val createdOn: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    val updatedOn: Long = System.currentTimeMillis()
)