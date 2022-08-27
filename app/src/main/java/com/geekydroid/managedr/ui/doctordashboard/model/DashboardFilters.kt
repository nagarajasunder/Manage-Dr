package com.geekydroid.managedr.ui.doctordashboard.model

import com.google.android.material.datepicker.MaterialDatePicker

data class DashboardFilters(
    val startDate:Long = MaterialDatePicker.thisMonthInUtcMilliseconds(),
    val endDate:Long = MaterialDatePicker.todayInUtcMilliseconds()
)