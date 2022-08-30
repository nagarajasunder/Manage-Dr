package com.geekydroid.managedr.utils.uiutils

import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker

object PickerUtils {

    fun getDatePicker(titleText: String? = null): MaterialDatePicker<Long> =
        MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText(titleText ?: "Select Date")
            .build()


    fun getDateRangePicker(
        titleText: String? = null,
        startDate: Long = MaterialDatePicker.thisMonthInUtcMilliseconds(),
        endDate: Long = MaterialDatePicker.todayInUtcMilliseconds(),
    ) =
        MaterialDatePicker.Builder.dateRangePicker()
            .setSelection(Pair(startDate, endDate))
            .setTitleText(titleText ?: "Select Date range")
            .build()

}