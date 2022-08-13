package com.geekydroid.managedr.ui.addnewservice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MDR_CITY")
data class MdrCity(
    @ColumnInfo(name = "city_id")
    @PrimaryKey(autoGenerate = true)
    val cityId:Int = 0,
    @ColumnInfo(name = "city_name")
    val cityName:String = "",
    @ColumnInfo(name = "created_on")
    val createdOn:Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_on")
    val updatedOn:Long = System.currentTimeMillis()
)