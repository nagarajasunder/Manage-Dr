package com.geekydroid.managedr.utils.uiutils

import com.google.android.material.datepicker.MaterialDatePicker

object PickerUtils {

    fun getDatePicker(titleText: String? = null): MaterialDatePicker<Long> {
        val picker = MaterialDatePicker.Builder.datePicker().setTitleText(titleText?:"Select Date").build()
        return picker
    }
}