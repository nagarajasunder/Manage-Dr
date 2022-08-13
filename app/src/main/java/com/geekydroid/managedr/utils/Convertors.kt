package com.geekydroid.managedr.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.*

//Room Type Convertors

class Convertors {

    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return if (value == null) {
            null
        } else {
            Date(value)
        }
    }

    @TypeConverter
    fun dateToTimestamp(value: Date?): Long? {
        return value?.time

    }

}