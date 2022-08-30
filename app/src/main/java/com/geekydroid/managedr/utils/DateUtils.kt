package com.geekydroid.managedr.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun fromLongToDateString(timestamp: Long, dateFormat: String = "dd-MM-yyyy"): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(timestamp)
    }

    fun fromLongToDate(value: Long?): Date? {
        value?.let {
            if (it == 0L) {
                return null
            }
            return Date(it)
        }
        return null
    }

}